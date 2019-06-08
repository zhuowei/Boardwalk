package net.zhuoweizhang.makeshift.javax.sound.sampled;

public class AudioFormat {
	protected float sampleRate;
	protected int sampleSizeInBits;
	protected int channels;
	protected boolean signed;
	protected boolean bigEndian;

	public AudioFormat(float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian) {
		this.sampleRate = sampleRate;
		this.sampleSizeInBits = sampleSizeInBits;
		this.channels = channels;
		this.signed = signed;
		this.bigEndian = bigEndian;
	}

	public float getSampleRate() {
		return sampleRate;
	}

	public int getSampleSizeInBits() {
		return sampleSizeInBits;
	}

	public int getChannels() {
		return channels;
	}

	public boolean isBigEndian() {
		return bigEndian;
	}
}
