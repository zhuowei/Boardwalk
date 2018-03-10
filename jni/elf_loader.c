// Copyright (c) 2011 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#define _GNU_SOURCE

#include <elf.h>
#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include <sys/mman.h>
#include <unistd.h>

#if defined(__x86_64__)
# define ElfW(name) Elf64_##name
# define ElfW_ELFCLASS ELFCLASS64
# define ElfW_EXPECTED_MACHINE EM_X86_64
#elif defined(__i386__)
# define ElfW(name) Elf32_##name
# define ElfW_ELFCLASS ELFCLASS32
# define ElfW_EXPECTED_MACHINE EM_386
#elif defined(__arm__)
# define ElfW(name) Elf32_##name
# define ElfW_ELFCLASS ELFCLASS32
# define ElfW_EXPECTED_MACHINE EM_ARM
#else
# error Unsupported target platform
#endif

#include <stdlib.h>
#include <errno.h>
#include <sched.h>
#include <unistd.h>
#include <android/log.h>

#define asm __asm

static void ElfLoader_die(const char* err) {
	__android_log_print(ANDROID_LOG_ERROR, "MasterPotato", "%s", err);
	abort();
}

static uintptr_t PageSizeRoundDown(uintptr_t val) {
  return val & ~(getpagesize() - 1);
}

static uintptr_t PageSizeRoundUp(uintptr_t val) {
  return PageSizeRoundDown(val + getpagesize() - 1);
}

static ElfW(auxv_t) *FindAuxv(int argc, char **argv) {
  char **ptr = argv + argc + 1;
  // Skip over envp.
  while (*ptr != NULL) {
    ptr++;
  }
  ptr++;
  return (ElfW(auxv_t) *) ptr;
}

static void SetAuxvField(ElfW(auxv_t) *auxv, unsigned type, uintptr_t value) {
  for (; auxv->a_type != AT_NULL; auxv++) {
    if (auxv->a_type == type) {
      auxv->a_un.a_val = value;
      return;
    }
  }
  printf("Cannot set auxv field: %i\n", type);
}

static void JumpToElfEntryPoint(void *stack, void *entry_point,
                                void *atexit_func) {
#if defined(__x86_64__)
  asm("mov %0, %%rsp\n"
      "jmp *%1\n"
      // %edx is registered as an atexit handler if non-zero.
      : : "r"(stack), "r"(entry_point), "d"(atexit_func));
#elif defined(__i386__)
  asm("mov %0, %%esp\n"
      "jmp *%1\n"
      // %rdx is registered as an atexit handler if non-zero.
      : : "r"(stack), "r"(entry_point), "d"(atexit_func));
#elif defined(__arm__)
  asm("mov sp, %0\n"
      "blx %1\n"
      // Something(?) is registered as an atexit handler if non-zero.
      : : "r"(stack), "r"(entry_point)/*, "d"(atexit_func)*/);
#else
# error Unsupported target platform
#endif
}

static void *LoadElfObject(int fd, ElfW(auxv_t) *auxv) {
  // Load and check headers.
  ElfW(Ehdr) elf_header;
  if (pread(fd, &elf_header, sizeof(elf_header), 0) != sizeof(elf_header)) {
    ElfLoader_die("Failed to read ELF header");
  }
  if (memcmp(elf_header.e_ident, ELFMAG, SELFMAG) != 0) {
    ElfLoader_die("Not an ELF file");
  }
  if (elf_header.e_ident[EI_CLASS] != ElfW_ELFCLASS) {
    ElfLoader_die("Unexpected ELFCLASS");
  }
  if (elf_header.e_machine != ElfW_EXPECTED_MACHINE) {
    ElfLoader_die("Unexpected ELF machine type");
  }
  if (elf_header.e_phentsize != sizeof(ElfW(Phdr))) {
    ElfLoader_die("Unexpected ELF program header entry size");
  }
  if (elf_header.e_phnum >= 20) {
    // We impose an arbitrary limit as a sanity check and to avoid
    // overflowing the stack.
    ElfLoader_die("Too many ELF program headers");
  }
  ElfW(Phdr) phdrs[elf_header.e_phnum];
  if (pread(fd, phdrs, sizeof(phdrs), elf_header.e_phoff)
      != (ssize_t) sizeof(phdrs)) {
    ElfLoader_die("Failed to read ELF program headers");
  }

  // Scan program headers to find the overall size of the ELF object.
  // Find the first and last PT_LOAD segments.  ELF requires that
  // PT_LOAD segments be in ascending order of p_vaddr, so we can use
  // the last one to calculate the whole address span of the image.
  size_t index = 0;
  while (index < elf_header.e_phnum && phdrs[index].p_type != PT_LOAD) {
    index++;
  }
  if (index == elf_header.e_phnum) {
    ElfLoader_die("ELF object contains no PT_LOAD headers");
  }
  ElfW(Phdr) *first_segment = &phdrs[index];
  ElfW(Phdr) *last_segment = &phdrs[elf_header.e_phnum - 1];
  while (last_segment > first_segment && last_segment->p_type != PT_LOAD) {
    last_segment--;
  }
  uintptr_t overall_start = PageSizeRoundDown(first_segment->p_vaddr);
  uintptr_t overall_end = PageSizeRoundUp(last_segment->p_vaddr
                                          + last_segment->p_memsz);
  uintptr_t overall_size = overall_end - overall_start;

  // Reserve address space.
  // Executables that must be loaded at a fixed address have an e_type
  // of ET_EXEC.  For these, we could use MAP_FIXED, but if the
  // address range is already occupied then that will clobber the
  // existing mappings without warning, which is bad.  Instead, use an
  // address hint and check that we got the expected address.
  // Executables that can be loaded at any address have an e_type of
  // ET_DYN.
  char *required_start =
    elf_header.e_type == ET_EXEC ? (char *) overall_start : NULL;
  char *base_addr = (char *) mmap(required_start, overall_size, PROT_NONE,
                                  MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
  if (base_addr == MAP_FAILED) {
    ElfLoader_die("Failed to reserve address space");
  }
  if (elf_header.e_type == ET_EXEC && base_addr != required_start) {
    ElfLoader_die("Failed to reserve address space at fixed address");
  }

  char *load_offset = (char *) (base_addr - required_start);
  char *entry_point = load_offset + elf_header.e_entry;
  SetAuxvField(auxv, AT_ENTRY, (uintptr_t) entry_point);
  SetAuxvField(auxv, AT_BASE, (uintptr_t) load_offset);
  SetAuxvField(auxv, AT_PHNUM, elf_header.e_phnum);
  SetAuxvField(auxv, AT_PHENT, elf_header.e_phentsize);
  // Note that this assumes that the program headers are included in a
  // PT_LOAD segment for which the file offsets matches the mapping
  // offset, but Linux assumes this too when setting AT_PHDR.
  SetAuxvField(auxv, AT_PHDR, (uintptr_t) base_addr + elf_header.e_phoff);

  for (ElfW(Phdr) *segment = first_segment;
       segment <= last_segment;
       segment++) {
    if (segment->p_type == PT_LOAD) {
      uintptr_t segment_start = PageSizeRoundDown(segment->p_vaddr);
      uintptr_t segment_end = PageSizeRoundUp(segment->p_vaddr
                                              + segment->p_memsz);
      int prot = 0;
      if ((segment->p_flags & PF_R) != 0)
        prot |= PROT_READ;
      if ((segment->p_flags & PF_W) != 0)
        prot |= PROT_WRITE;
      if ((segment->p_flags & PF_X) != 0)
        prot |= PROT_EXEC;
      void *result = mmap(load_offset + segment_start,
                          segment_end - segment_start,
                          prot, MAP_PRIVATE | MAP_FIXED, fd,
                          PageSizeRoundDown(segment->p_offset));
      if (result == MAP_FAILED) {
        ElfLoader_die("Failed to map ELF segment");
      }
      // TODO(mseaborn): Support a BSS that goes beyond the file's extent.
      if ((segment->p_flags & PF_W) != 0) {
        // Zero the BSS to the end of the page.  ld.so and other
        // programs use the rest of this page as part of the brk()
        // heap and assume that it has been zeroed.
        uintptr_t bss_start = segment->p_vaddr + segment->p_filesz;
        memset(load_offset + bss_start, 0, segment_end - bss_start);
      }
    }
  }
  if (close(fd) != 0) {
    ElfLoader_die("close() failed");
  }
  return entry_point;
}

extern char **environ;

static int CountEnviron() {
	int environcount = 0;
	char** e = environ;
	while(*e++) {
		environcount++;
	}
	return environcount;
}

static void SetupBrk() {
  if (sbrk(4*1024*1024) == (void*) -1) {
    ElfLoader_die("Sbrk failed");
  }
}
extern void hack();
void dump_entry(int argc, char** argv, char** environ) {
    __android_log_print(ANDROID_LOG_ERROR, "Boardwalk", "argc: %d", argc);
for (int i = 0; i < argc; i++) {
    __android_log_print(ANDROID_LOG_ERROR, "Boardwalk", "argv: %d=%s", i, argv[i]);
}
int envcount = 0;
for (int i = 0; environ[i]; i++) {
    __android_log_print(ANDROID_LOG_ERROR, "Boardwalk", "environ: %d=%s", i, environ[i]);
envcount++;
}
ElfW(auxv_t)* auxv = (ElfW(auxv_t)*) (environ + envcount + 1);
for (int i = 0; ; i++) {
    __android_log_print(ANDROID_LOG_ERROR, "Boardwalk", "auxv: %d=%x", auxv[i].a_type, auxv[i].a_un.a_val);
	if (auxv[i].a_type == AT_NULL) break;
}
abort();
}
int PotatoExec(void* src_auxv, size_t src_auxv_size,
	int argc, char **argv) {
  printf("PotatoExec: %d %s\n", argc, argv[0]);
  if (argc < 1) {
    //fprintf(stderr, "Usage: %s executable args...\n", argv[0]);
    return 1;
  }

  const char *executable_filename = argv[0];
  int executable_fd = open(executable_filename, O_RDONLY);
  if (executable_fd < 0) {
    __android_log_print(ANDROID_LOG_ERROR, "MasterPotato", "Failed to open executable %s: %s\n",
            executable_filename, strerror(errno));
    return 1;
  }

  /*
  playground::g_policy.allow_file_namespace = true;
  playground::AddTlsSetupSyscall();
  StartSeccompSandbox();
  */

  int environcount = CountEnviron();

  int infoblock_entries = 
	1 +		// argc
	argc + 1 + 	// argv (and terminating zero)
	environcount + 1 + // environ (and terminating zero)
	src_auxv_size / sizeof(ElfW(auxv_t))	// auxv
	;
  size_t stackSize = 8*1024*1024; // 8 MB
  size_t stackAlloc = PageSizeRoundUp(infoblock_entries * sizeof(void*)) +
	stackSize;
  __android_log_print(ANDROID_LOG_ERROR, "Boardwalk", "infoblock_entries %d stackAlloc %x\n", infoblock_entries, stackAlloc);
  char** stack = mmap(NULL, stackAlloc, PROT_READ|PROT_WRITE, MAP_GROWSDOWN|MAP_ANONYMOUS|MAP_PRIVATE, -1, 0);
  //printf("New stack: %p old environ: %p\n", stack, environ);

  stack += (stackSize / sizeof(void*)); // give one extra page in front of the exec() data

  //char **stack = argv;
  *(long *) stack = argc;
  memcpy(stack + 1, argv, (argc + 1)*sizeof(void*));
  memcpy(stack + 1 + (argc + 1), environ, (environcount + 1)*sizeof(void*));
  memcpy(stack + 1 + (argc + 1) + (environcount + 1), src_auxv, src_auxv_size);

  //ElfW(auxv_t) *auxv = FindAuxv(argc, argv);
  ElfW(auxv_t) *auxv = (ElfW(auxv_t) *) (stack + 1 + (argc + 1) + (environcount + 1));
  /*
  SetAuxvField(auxv, AT_SYSINFO, (uintptr_t) syscallEntryPointNoFrame);
  */
  /* Android Oreo's Zygote is setuid. Clear AT_SECURE. */
  SetAuxvField(auxv, AT_SECURE, 0);

  void *entry_point = LoadElfObject(executable_fd, auxv);
  //__android_log_print(ANDROID_LOG_ERROR, "MasterPotato", "Entry point: %p auxv %p\n", entry_point, auxv);

  //SetupBrk(); // and hope the original app's done expanding its heap
//entry_point = &hack;
  __android_log_print(ANDROID_LOG_ERROR, "MasterPotato", "Entry %p hack %p dump_entry %p\n", entry_point, &hack, &dump_entry);
  JumpToElfEntryPoint(stack, entry_point, 0);
}
