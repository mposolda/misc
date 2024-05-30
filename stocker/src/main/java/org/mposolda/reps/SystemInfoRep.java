package org.mposolda.reps;

import org.mposolda.services.Services;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SystemInfoRep {

    private String startTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public SystemInfoRep copy() {
        SystemInfoRep newSys = new SystemInfoRep();
        newSys.startTime = this.startTime;
        return newSys;
    }
}
