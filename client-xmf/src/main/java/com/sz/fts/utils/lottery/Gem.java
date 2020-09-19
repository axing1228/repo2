package com.sz.fts.utils.lottery;

public class Gem {
	/** 中奖主键 **/
	private int rowid;
	/** 奖品名称 */
	private String name;
	/** 奖品概率 */
	private int priority;

	public Gem() {
		super();
	}

	public Gem(int rowid, String name, int priority) {
		super();
		this.rowid = rowid;
		this.name = name;
		this.priority = priority;
	}

	@Override
	public String toString() {
		return "Gum [rowid=" + rowid + ",name=" + name + ", priority=" + priority + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getRowid() {
		return rowid;
	}

	public void setRowid(int rowid) {
		this.rowid = rowid;
	}
}
