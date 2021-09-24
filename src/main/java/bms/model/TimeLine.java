package bms.model;

import java.util.*;

/**
 * タイムライン
 * 
 * @author exch
 */
public class TimeLine {
	
	/**
	 * タイムラインの時間(us)
	 */
	private long time;
	/**
	 * タイムラインの小節
	 */
	private double section;
	/**
	 * タイムライン上に配置されている演奏レーン分のノート。配置されていないレーンにはnullを入れる。
	 */
	private bms.model.Note[] notes;

	/**
	 * タイムライン上に配置されている演奏レーン分の不可視ノート。配置されていないレーンにはnullを入れる。
	 */
	private bms.model.Note[] hiddennotes;
	/**
	 * タイムライン上に配置されているBGMノート
	 */
	private bms.model.Note[] bgnotes = bms.model.Note.EMPTYARRAY;
	/**
	 * 小節線の有無
	 */
	private boolean sectionLine = false;
	/**
	 * タイムライン上からのBPM変化
	 */
	private double bpm;
	/**
	 * ストップ時間(us)
	 */
	private long stop;
	/**
	 * スクロールスピード
	 */
	private double scroll = 1.0;
	
	/**
	 * 表示するBGAのID
	 */
	private int bga = -1;
	/**
	 * 表示するレイヤーのID
	 */
	private int layer = -1;
	/**
	 * POORレイヤー
	 */
	private bms.model.Layer[] eventlayer = bms.model.Layer.EMPTY;

	public TimeLine(double section, long time, int notesize) {
		this.section = section;
		this.time = time;
		notes = new bms.model.Note[notesize];
		hiddennotes = new bms.model.Note[notesize];
	}

	public int getTime() {
		return (int) (time / 1000);
	}
	
	public long getMilliTime() {
		return time / 1000;
	}

	public long getMicroTime() {
		return time;
	}

	protected void setTime(long time) {
		this.time = time;
		for(bms.model.Note n : notes) {
			if(n != null) {
				n.setTime(time);
			}
		}
		for(bms.model.Note n : hiddennotes) {
			if(n != null) {
				n.setTime(time);
			}
		}
		for(bms.model.Note n : bgnotes) {
			n.setTime(time);
		}
	}

	public int getLaneCount() {
		return notes.length;
	}
	
	protected void setLaneCount(int lanes) {
		if(notes.length != lanes) {
			bms.model.Note[] newnotes = new bms.model.Note[lanes];
			bms.model.Note[] newhiddennotes = new bms.model.Note[lanes];
			for(int i = 0;i < lanes;i++) {
				if(i < notes.length) {
					newnotes[i] = notes[i];
					newhiddennotes[i] = hiddennotes[i];
				}
			}
			notes = newnotes;
			hiddennotes = newhiddennotes;
		}
	}

	/**
	 * タイムライン上の総ノート数を返す
	 * 
	 * @return
	 */
	public int getTotalNotes() {
		return getTotalNotes(bms.model.BMSModel.LNTYPE_LONGNOTE);
	}

	/**
	 * タイムライン上の総ノート数を返す
	 * 
	 * @return
	 */
	public int getTotalNotes(int lntype) {
		int count = 0;
		for (bms.model.Note note : notes) {
			if (note != null) {
				if (note instanceof bms.model.LongNote) {
					final bms.model.LongNote ln = (bms.model.LongNote) note;
					if (ln.getType() == bms.model.LongNote.TYPE_CHARGENOTE || ln.getType() == bms.model.LongNote.TYPE_HELLCHARGENOTE
                        || (ln.getType() == LongNote.TYPE_UNDEFINED && lntype != BMSModel.LNTYPE_LONGNOTE)
                        || !ln.isEnd()) {
						count++;
					}
				} else if (note instanceof NormalNote) {
					count++;
				}
			}
		}
		return count;
	}

	public boolean existNote() {
		for (bms.model.Note n : notes) {
			if (n != null) {
				return true;
			}
		}
		return false;
	}

	public boolean existNote(int lane) {
		return notes[lane] != null;
	}

	public bms.model.Note getNote(int lane) {
		return notes[lane];
	}

	public void setNote(int lane, bms.model.Note note) {
		notes[lane] = note;
		if(note == null) {
			return;
		}
		note.setSection(section);
		note.setTime(time);
	}

	public void setHiddenNote(int lane, bms.model.Note note) {
		hiddennotes[lane] = note;
		if(note == null) {
			return;
		}
		note.setSection(section);
		note.setTime(time);
	}

	public boolean existHiddenNote() {
		for (bms.model.Note n : hiddennotes) {
			if (n != null) {
				return true;
			}
		}
		return false;
	}

	public bms.model.Note getHiddenNote(int lane) {
		return hiddennotes[lane];
	}

	public void addBackGroundNote(bms.model.Note note) {
		if(note == null) {
			return;
		}
		note.setSection(section);
		note.setTime(time);
		bgnotes = Arrays.copyOf(bgnotes, bgnotes.length + 1);
		bgnotes[bgnotes.length - 1] = note;
	}

	public void removeBackGroundNote(bms.model.Note note) {
		for(int i = 0;i < bgnotes.length;i++) {
			if(bgnotes[i] == note) {
				final bms.model.Note[] newbg = new bms.model.Note[bgnotes.length - 1];
				for(int j = 0, index = 0;j < bgnotes.length;j++) {
					if(i != j) {
						newbg[index] = bgnotes[j];
						index++;
					}
				}
				bgnotes = newbg;
				break;
			}
		}
	}

	public bms.model.Note[] getBackGroundNotes() {
		return bgnotes;
	}

	public void setBPM(double bpm) {
		this.bpm = bpm;
	}

	public double getBPM() {
		return bpm;
	}

	public void setSectionLine(boolean section) {
		this.sectionLine = section;
	}

	public boolean getSectionLine() {
		return sectionLine;
	}

	/**
	 * 表示するBGAのIDを取得する
	 * 
	 * @return BGAのID
	 */
	public int getBGA() {
		return bga;
	}

	/**
	 * 表示するBGAのIDを設定する
	 * 
	 * @param bga
	 *            BGAのID
	 */
	public void setBGA(int bga) {
		this.bga = bga;
	}

	/**
	 * 表示するレイヤーBGAのIDを取得する
	 * 
	 * @return レイヤーBGAのID
	 */
	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public bms.model.Layer[] getEventlayer() {
		return eventlayer;
	}

	public void setEventlayer(Layer[] eventlayer) {
		this.eventlayer = eventlayer;
	}

	public double getSection() {
		return section;
	}

	public void setSection(double section) {
		for(bms.model.Note n : notes) {
			if(n != null) {
				n.setSection(section);					
			}
		}
		for(bms.model.Note n : hiddennotes) {
			if(n != null) {
				n.setSection(section);					
			}
		}
		for(Note n : bgnotes) {
			n.setSection(section);
		}
		this.section = section;
	}

	public int getStop() {
		return (int) (stop / 1000);
	}
	
	public long getMilliStop() {
		return stop / 1000;
	}

	public long getMicroStop() {
		return stop;
	}

	public void setStop(long stop) {
		this.stop = stop;
	}
	
	public double getScroll() {
		return scroll;
	}

	public void setScroll(double scroll) {
		this.scroll = scroll;
	}
}