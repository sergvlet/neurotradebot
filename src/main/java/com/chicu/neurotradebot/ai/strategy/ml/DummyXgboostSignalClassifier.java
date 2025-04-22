package com.chicu.neurotradebot.ai.strategy.ml;

import com.chicu.neurotradebot.trade.enums.Signal;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Заглушка XGBoost классификатора.
 */
@Component
public class DummyXgboostSignalClassifier implements SignalClassifier {

    @Override
    public Signal classify(List<Double> priceHistory) {
        // Простая заглушка: если последняя цена > предпоследней — BUY
        int last = priceHistory.size() - 1;
        if (priceHistory.get(last) > priceHistory.get(last - 1)) return Signal.BUY;
        if (priceHistory.get(last) < priceHistory.get(last - 1)) return Signal.SELL;
        return Signal.HOLD;
    }
}
