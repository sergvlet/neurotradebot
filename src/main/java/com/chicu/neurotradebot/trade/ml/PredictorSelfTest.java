//// src/main/java/com/chicu/neurotradebot/trade/ml/PredictorSelfTest.java
//package com.chicu.neurotradebot.trade.ml;
//
//import com.chicu.neurotradebot.entity.AiTradeSettings;
//import com.chicu.neurotradebot.service.AiTradeSettingsService;
//import com.chicu.neurotradebot.trade.ml.strategy.IndicatorCalculator.IndicatorValues;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class PredictorSelfTest implements CommandLineRunner {
//
//    private final TpSlPredictor predictor;
//    private final AiTradeSettingsService settingsService;
//
//    @Override
//    public void run(String... args) {
//        AiTradeSettings settings = settingsService.getForCurrentUser();
//        String url = settings.getMlStrategyConfig().getPredictUrl();
//
//        // Заполняем все шесть полей: rsi, bbUpper, bbLower, bbWidth, atr, bodyRatio
//        IndicatorValues iv = new IndicatorValues(
//            30.5,   // rsi
//            1.05,   // bbUpper  (пример)
//            0.95,   // bbLower  (пример)
//            10.0,   // bbWidth  (пример, = (1.05-0.95)/1.0*100)
//            0.003,  // atr
//            45.0    // bodyRatio
//        );
//
//        TpSlResult res = predictor.predict(iv, url);
//        log.info("=== SELF-TEST TpSlPredictor: TP={}%, SL={}%",
//                 res.getTpPercent(), res.getSlPercent());
//    }
//}
