.syntax unified
.arm
.global hack
hack:
	ldr r0, [sp, #0]
	add r1, sp, #4
	add r2, r1, r0, lsl 2
	add r2, r2, 4
	blx dump_entry
