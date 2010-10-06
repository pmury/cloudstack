/**
 *  Copyright (C) 2010 Cloud.com, Inc.  All rights reserved.
 * 
 * This software is licensed under the GNU General Public License v3 or later.
 * 
 * It is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package com.cloud.api;

import com.cloud.api.response.AsyncJobResponse;
import com.cloud.async.AsyncJobManager;
import com.cloud.async.AsyncJobVO;

/**
 * A base command for supporting asynchronous API calls.  When an API command is received, the command will be
 * serialized to the queue (currently the async_job table) and a response will be immediately returned with the
 * id of the queue object.  The id can be used to query the status/progress of the command using the
 * queryAsyncJobResult API command.
 */
public abstract class BaseAsyncCmd extends BaseCmd {
    private AsyncJobManager _asyncJobMgr = null;
    private AsyncJobVO _job = null;

    @Parameter(name="starteventid", type=CommandType.LONG)
    private Long startEventId;

    /**
     * For async commands the API framework needs to know the owner of the object being acted upon.  This method is
     * used to determine that information.
     * @return the id of the account that owns the object being acted upon
     */
    public abstract long getAccountId();

    /**
     * For proper tracking of async commands through the system, events must be generated when the command is
     * scheduled, started, and completed.  Commands should specify the type of event so that when the scheduled,
     * started, and completed events are saved to the events table, they have the proper type information.
     * @return a string representing the type of event, e.g. VM.START, VOLUME.CREATE.
     */
    public abstract String getEventType();

    /**
     * For proper tracking of async commands through the system, events must be generated when the command is
     * scheduled, started, and completed.  Commands should specify a description for these events so that when
     * the scheduled, started, and completed events are saved to the events table, they have a meaningful description.
     * @return a string representing a description of the event
     */
    public abstract String getEventDescription();

    public ResponseObject getResponse(long jobId) {
        AsyncJobResponse response = new AsyncJobResponse();
        response.setId(jobId);
        response.setResponseName(getName());
        return response;
    }

    public AsyncJobManager getAsyncJobManager() {
        return _asyncJobMgr;
    }

    public void setAsyncJobManager(AsyncJobManager mgr) {
        _asyncJobMgr = mgr;
    }

    public void synchronizeCommand(String syncObjType, long syncObjId) {
        _asyncJobMgr.syncAsyncJobExecution(_job, syncObjType, syncObjId);
    }

    public AsyncJobVO getJob() {
        return _job;
    }

    public void setJob(AsyncJobVO job) {
        _job = job;
    }

    public Long getStartEventId() {
        return startEventId;
    }

    public void setStartEventId(Long startEventId) {
        this.startEventId = startEventId;
    }
}
