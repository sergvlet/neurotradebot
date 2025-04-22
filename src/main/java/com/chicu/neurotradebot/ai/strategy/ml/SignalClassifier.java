package com.chicu.neurotradebot.ai.strategy.ml;

import com.chicu.neurotradebot.trade.model.Signal;

import java.util.List;

public interface SignalClassifier {
    Signal classify(List<Double> priceHistory);
}
