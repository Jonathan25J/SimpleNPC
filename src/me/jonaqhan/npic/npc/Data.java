package me.jonaqhan.npic.npc;

public class Data {

	public String npcName;
	public String creator;
	public String skin;
	public Double x;
	public Double y;
	public Double z;
	public Float pitch;

	public Data(String npcName, String creator, String skin, Double x, Double y, Double z, Float pitch, Float yaw,
			String world, String texture, String signature) {
		super();
		this.npcName = npcName;
		this.creator = creator;
		this.skin = skin;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.world = world;
		this.texture = texture;
		this.signature = signature;
	}

	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

	public Float yaw;
	public String world;
	public String texture;
	public String signature;

	public String getNpcName() {
		return npcName;
	}

	public void setNpcName(String npcName) {
		this.npcName = npcName;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public Double getZ() {
		return z;
	}

	public void setZ(Double z) {
		this.z = z;
	}

	public Float getPitch() {
		return pitch;
	}

	public void setPitch(Float pitch) {
		this.pitch = pitch;
	}

	public Float getYaw() {
		return yaw;
	}

	public void setYaw(Float yaw) {
		this.yaw = yaw;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public String getTexture() {
		return texture;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

}
