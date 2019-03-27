package unit.utils;

import com.gala.sam.tradeEngine.domain.*;
import com.gala.sam.tradeEngine.domain.Order.DIRECTION;
import com.gala.sam.tradeEngine.domain.Order.TIME_IN_FORCE;
import com.gala.sam.tradeEngine.utils.OrderCSVParser;
import com.gala.sam.tradeEngine.utils.TradeCSVParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CSVTests {

    private final static String csvInputHeader = "ORDER ID,GROUP ID,DIRECTION,QUANTITY,TICKER,TYPE,LIMIT PRICE,TIME IN FORCE,TRIGGER PRICE";
    private final static String csvOutputHeader = "BUY ORDER,SELL ORDER,MATCH QTY,MATCH PRICE,TICKER";

    @Test
    public void canDecodeCSVStopLimitOrder() {

        final String limitOrderInput = "42,1,BUY,999,Fred,STOP-LIMIT,3.14,GTC,666";
        final Order limitOrderOutput = StopLimitOrder.builder()
                .triggerPrice(666)
                .orderId(42)
                .direction(DIRECTION.BUY)
                .quantity(999)
                .ticker("Fred")
                .limit(3.14f)
                .timeInForce(TIME_IN_FORCE.GTC)
                .build();

        final List<String> csvInput = new ArrayList<>();
        csvInput.add(csvInputHeader);
        csvInput.add(limitOrderInput);

        List<Order> orders = OrderCSVParser.decodeCSV(csvInput);

        Assert.assertEquals("Decoder should decode a stop limit order", 1, orders.size());
        Assert.assertEquals("Decoder should decode a stop limit order correctly", orders.get(0),
            limitOrderOutput);
    }

    @Test
    public void canEncodeTradeToCSV() {
        final Trade tradeInput = Trade.builder()
            .buyOrder(64)
            .sellOrder(118)
            .matchPrice(1.5f)
            .matchQuantity(451)
            .ticker("FRED")
            .build();

        final String tradeOutput = "64,118,451,1.5,FRED";

        final List<String> outputTest = new ArrayList<>();
        outputTest.add(csvOutputHeader);
        outputTest.add(tradeOutput);

        final List<Trade> inputTrades = new LinkedList<>();
        inputTrades.add(tradeInput);

        final List<String> output = TradeCSVParser.encodeCSV(inputTrades);

        Assert.assertEquals("Encoder should encode a trade correctly", output, outputTest);
    }

}
