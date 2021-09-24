package bms.model;

import bms.model.BMSModel;
import bms.model.TimeLine;

import java.util.ArrayDeque;
import java.util.Collection;

public class EventLane {

	private bms.model.TimeLine[] sections;
	private int sectionbasepos;
	private int sectionseekpos;

	private bms.model.TimeLine[] bpms;
	private int bpmbasepos;
	private int bpmseekpos;

	private bms.model.TimeLine[] stops;
	private int stopbasepos;
	private int stopseekpos;

	public EventLane(BMSModel model) {
		Collection<bms.model.TimeLine> section = new ArrayDeque<bms.model.TimeLine>();
		Collection<bms.model.TimeLine> bpm = new ArrayDeque<bms.model.TimeLine>();
		Collection<bms.model.TimeLine> stop = new ArrayDeque<bms.model.TimeLine>();
		
		bms.model.TimeLine prev = null;
		for (bms.model.TimeLine tl : model.getAllTimeLines()) {
			if (tl.getSectionLine()) {
				section.add(tl);
			}
			if (tl.getBPM() != (prev != null ? prev.getBPM() : model.getBpm())) {
				bpm.add(tl);
			}
			if (tl.getStop() != 0) {
				stop.add(tl);
			}
			prev = tl;
		}
		sections = section.toArray(new bms.model.TimeLine[section.size()]);
		bpms = bpm.toArray(new bms.model.TimeLine[bpm.size()]);
		stops = stop.toArray(new bms.model.TimeLine[stop.size()]);
	}

	public bms.model.TimeLine[] getSections() {
		return sections;
	}

	public bms.model.TimeLine[] getBpmChanges() {
		return bpms;
	}
	
	public bms.model.TimeLine[] getStops() {
		return stops;
	}
	
	public bms.model.TimeLine getSection() {
		if (sectionseekpos < sections.length) {
			return sections[sectionseekpos++];
		}
		return null;
	}

	public bms.model.TimeLine getBpm() {
		if (bpmseekpos < bpms.length) {
			return bpms[bpmseekpos++];
		}
		return null;
	}
	
	public TimeLine getStop() {
		if (stopseekpos < stops.length) {
			return stops[stopseekpos++];
		}
		return null;
	}
	
	public void reset() {
		sectionseekpos = sectionbasepos;
		bpmseekpos = bpmbasepos;
		stopseekpos = stopbasepos;
	}

	public void mark(int time) {
		for (; sectionbasepos < sections.length - 1 && sections[sectionbasepos + 1].getTime() > time; sectionbasepos++)
			;
		for (; sectionbasepos > 0 && sections[sectionbasepos].getTime() < time; sectionbasepos--)
			;
		for (; bpmbasepos < bpms.length - 1 && bpms[bpmbasepos + 1].getTime() > time; bpmbasepos++)
			;
		for (; bpmbasepos > 0 && bpms[bpmbasepos].getTime() < time; bpmbasepos--)
			;
		for (; stopbasepos < stops.length - 1 && stops[stopbasepos + 1].getTime() > time; stopbasepos++)
			;
		for (; stopbasepos > 0 && stops[stopbasepos].getTime() < time; stopbasepos--)
			;
		sectionseekpos = sectionbasepos;
		bpmseekpos = bpmbasepos;
		stopseekpos = stopbasepos;
	}
	

}
