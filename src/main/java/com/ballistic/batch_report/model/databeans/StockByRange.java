package com.ballistic.batch_report.model.databeans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.Set;

/**
 * Created by AdMaxim on 5/24/2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockByRange {

    public Set<String> userUuid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date endDate;

    public StockByRange() {}

    public StockByRange(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Set<String> getUserUuid() { return userUuid; }
    public void setUserUuid(Set<String> userUuid) { this.userUuid = userUuid; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

}
