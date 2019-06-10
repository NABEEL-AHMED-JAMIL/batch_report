package com.ballistic.batch_report.coredel.pojo;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;
import java.util.Date;

@Data
@Entity("StockPrice")
public class StockPrice {
	
    @Id
    @Property("_id")
    private String uuId;
    private Date date;
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double closePrice;
    private Double wap;
    private Integer noOfShares;
    private Integer noOfTrades;
    private Double totalTurnover;
    private Integer deliverableQuantity;
    private Double deliQtyToTradedQty;
    private Double spreadHighLow;
    private Double spreadCloseOpen;
    private Date createDate;
    private Date updateDate;

}
