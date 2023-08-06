package org.siniuk.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InvoiceMessages {

    SINGLE("1️⃣ Разовая: расчёт 1 энергии на выбор для одной даты.", 29900),
    YEAR("2️⃣ Подписка на год: до 6 запросов расчёта энергии года и 2 энергий месяца.", 250000),
    FOREVER("3️⃣ Подписка навсегда: безвременный доступ к расчёту всех энергий до 6 запросов в месяц + бонус «Денежные энергии».", 710000);

    private final String description;
    private final Integer price;
}
