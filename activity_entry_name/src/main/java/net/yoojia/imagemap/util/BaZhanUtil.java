package net.yoojia.imagemap.util;

public class BaZhanUtil {
	
	public float initAngle = 0;
	
	public float initScale = 1f;
	
	public BaZhanUtil(float initAngle, float initScale) {
		super();
		this.initAngle = initAngle;
		this.initScale = initScale;
	}

	public float getInitAngle() {
		return initAngle;
	}

	public void setInitAngle(float initAngle) {
		this.initAngle = initAngle;
	}

	public float getInitScale() {
		return initScale;
	}

	public void setInitScale(float initScale) {
		this.initScale = initScale;
	}

	@Override
	public String toString() {
		return "BaZhanUtil [initAngle=" + initAngle + ", initScale="
				+ initScale + "]";
	}

}
