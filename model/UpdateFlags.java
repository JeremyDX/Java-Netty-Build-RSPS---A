/*
* @ Author - Digistr
* @ Objective Contains All Update Information.
*/
package com.model;

public class UpdateFlags {

	private int faceTo;
	private int gfxData;
	private short gfxId;
	private short forcedGfx = -1;
	private short index;
	private short faceDirectionX;
	private short faceDirectionY;
	private short animationId;
	private short forcedAnimation = -1;
	private byte animationDelay;
	private byte hitDamage1;
	private byte hitDamage2;
	private byte hitColor1;
	private byte hitColor2;
	private String headText;
	private byte[] chatText;
	private short chatEffects;
	private short[] features = {-1, -1, 256, 259, 271, 274, 282, 289, 294, 298, 256, 256, 256, 257, 256};
	public boolean flagsAreUpdated[] = new boolean[14];

	protected UpdateFlags() {

	}

	public void setIndex(short index) {
		this.index = index;
	}
	
	public void reset() {
		for (int i = 0; i < 14; i++)
			flagsAreUpdated[i] = false;
		if (faceTo != 0)
			flagsAreUpdated[5] = true;
	}

	public void resetFaceTo() {
		faceTo = (short)-32768;
		flagsAreUpdated[5] = true;
		flagsAreUpdated[3] = true;		
	}

	public void setTurnToDirection(short x, short y) {
		faceDirectionX = x;
		faceDirectionY = y;
		flagsAreUpdated[4] = true;
		flagsAreUpdated[3] = true;
	}

	public void setFaceToPlayer(int faceTo) {
		this.faceTo = (short)(faceTo - 32768);
		flagsAreUpdated[5] = true;
		flagsAreUpdated[3] = true;
	}

	public void setFaceToNpc(short faceTo) {
		this.faceTo = faceTo;
		flagsAreUpdated[5] = true;
		flagsAreUpdated[3] = true;
	}

	public void hit(byte damage, byte color) {
		if (flagsAreUpdated[6]) {
			hitDamage2 = damage;
			hitColor2 = color;
			flagsAreUpdated[12] = true;
		} else {
			hitDamage1 = damage;
			hitColor1 = color;
			flagsAreUpdated[6] = true;
			flagsAreUpdated[3] = true;
		}
	}

	public void stopAnimation() {
		flagsAreUpdated[7] = false;	
	}

	public void setAnimation(int anim, int delay) {
		if (forcedAnimation != -1)
			return;
		animationId = (short)anim;
		animationDelay = (byte)delay;
		flagsAreUpdated[7] = true;
		flagsAreUpdated[3] = true;
	}

	public void setForcedAnimation(int anim) 
	{
		animationId = (short)anim;
		forcedAnimation = (short)anim;
		animationDelay = (byte)0;
		flagsAreUpdated[7] = true;
		flagsAreUpdated[3] = true;
	}

	public void resetorcedAnimation() 
	{
		forcedAnimation = -1;
	}
	
	public void resetForcedGfx()
	{
		forcedGfx = -1;
	}

	public void setLook(int slot, int look) {
		features[slot + 2] = (short)(look + 256);
		flagsAreUpdated[8] = true;
		flagsAreUpdated[3] = true;
	}

	public void setLooks(short[] looks) {
		for (int i = 0; i < looks.length; i++) {
			features[i+2] = (short)(looks[i] + 256);
		}
		flagsAreUpdated[8] = true;
		flagsAreUpdated[3] = true;
	}

	public void setHeadIcon(int slot, int icon) {
		features[slot] = (byte)icon;
		flagsAreUpdated[8] = true;
		flagsAreUpdated[3] = true;
	}

	public void forceAppearence() {
		flagsAreUpdated[8] = true;
		flagsAreUpdated[3] = true;
	}

	public void setText(String text) {
		headText = text;
		flagsAreUpdated[9] = true;
		flagsAreUpdated[3] = true;
	}

	public void setChat(byte[] chat, short effects) {
		chatText = chat;
		chatEffects = effects;
		flagsAreUpdated[10] = true;
		flagsAreUpdated[3] = true;
	}

	public void setGraphic(short id, int delay, int height) 
	{
		if (forcedGfx != -1)
			return;
		gfxId = id;
		gfxData = height << 16 | delay;
		flagsAreUpdated[11] = true;
		flagsAreUpdated[3] = true;
	}

	public void setForcedGraphic(short id, int height) 
	{
		forcedGfx = id;
		gfxId = id;
		gfxData = height << 16;
		flagsAreUpdated[11] = true;
		flagsAreUpdated[3] = true;
	}

	public int getFaceTo() {
		return faceTo;
	}

	public byte getHitDamage1() {
		return hitDamage1;
	}

	public byte getHitColor1() {
		return hitColor1;
	}

	public byte getHitDamage2() {
		return hitDamage2;
	}

	public byte getHitColor2() {
		return hitColor2;
	}

	public short getGfxId() {
		return gfxId;
	}

	public int getGfxData() {
		return gfxData;
	}

	public short getAnimId() {
		return animationId;
	}

	public byte getAnimDelay() {
		return animationDelay;
	}

	public String getHeadText() {
		return headText;
	}

	public byte[] getChatText() {
		return chatText;
	}

	public short getChatEffects() {
		return chatEffects;
	}

	public short getDirectionX() {
		return faceDirectionX;
	}

	public short getDirectionY() {
		return faceDirectionY;
	}

	public short getFeature(int feature) {
		return features[feature];
	}

}