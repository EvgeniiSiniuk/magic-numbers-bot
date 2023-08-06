package org.siniuk.bot;

import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputInvoiceMessageContent;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;

import java.util.ArrayList;
import java.util.List;

public class InvoiceFactory {

    public static InputInvoiceMessageContent createInvoice(String subscription, Integer price, String payload) {
        String title = "Your Invoice Title";
        String description = "Description of your invoice";

        // Set the necessary price and currency information
        String currency = "RUB";
        List<LabeledPrice> prices = new ArrayList<>();
        prices.add(new LabeledPrice(subscription, price)); // Price in cents (e.g., 10 USD)
        return new InputInvoiceMessageContent(title, description, payload, "381764678:TEST:63269", currency, prices);
    }
}
