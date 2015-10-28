package com.www.server;

import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.io.FilenameUtils;

@XmlRootElement
public class Task {

    public String taskID, taskName, taskSize, taskStatus, className;
    public String timeStatus, timeFrom, timeTo;
    public String locStatus, locSWlat, locSWlng, locNElat, locNElng;

    public Task(String xmlUrl) {
        try {
            this.taskID = Utils.getText(xmlUrl, "id");
            this.taskName = Utils.getText(xmlUrl, "client", "name");
            this.taskSize = Utils.getText(xmlUrl, "client", "zip_size");
            this.taskStatus = Utils.getText(xmlUrl, "status");

            this.className = FilenameUtils.removeExtension(taskName);

            this.timeStatus = Utils.getText(xmlUrl, "client", "time", "status");
            if (!this.timeStatus.isEmpty()) {
                this.timeFrom = Utils.getText(xmlUrl, "client", "time", "from");
                this.timeTo = Utils.getText(xmlUrl, "client", "time", "to");
            }
            
            this.locStatus = Utils.getText(xmlUrl, "client", "map", "status");
            if (!this.locStatus.isEmpty()) {
                this.locSWlat = Utils.getText(xmlUrl, "client", "map", "sw_lat");
                this.locSWlng = Utils.getText(xmlUrl, "client", "map", "sw_lng");
                this.locNElat = Utils.getText(xmlUrl, "client", "map", "ne_lat");
                this.locNElng = Utils.getText(xmlUrl, "client", "map", "ne_lng");
            }
        } catch (Exception ex) {
            //Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
