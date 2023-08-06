package org.siniuk.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GeneralMessages {

    ENTER_DATE("Введи дату рождения по образцу: гггг-мм-дд Например, 1983-09-26"),
    ERROR_DATE("Упс, кажется, дата введена некорректно. Попробуй ещё раз – обязательно по образцу: гггг-мм-дд Например, 1983-09-26"),
    BONUS_MESSAGE("Чтобы получить бонус за безлимитную подписку, напиши в личные сообщения @tovanova_n слово «бонус». Я подготовлю тебе персональный расчёт твоих денежных архетипов в Матрице Судьбы \uD83D\uDCB6"),
    RENEW_MESSAGE("В текущем месяца израсходованы все запросы. Буду ждать тебя в следующем месяце. Или ты можешь добавить ещё одну подписку: "),
    SUCCESSFUL_SINGLE_PAYMENT("1️⃣ Оплата прошла успешно!\n" +
            "Твой тариф: Разовый расчёт \n"),
    SUCCESSFUL_YEAR_PAYMENT("2️⃣ Оплата прошла успешно!\n" +
            "Твой тариф: Подписка на год\n"),
    SUCCESSFUL_FOREVER_PAYMENT("3️⃣  Оплата прошла успешно!\n" +
            "Твой тариф: Подписка навсегда \n"),
    PAYMENT_ERROR("Что-то пошло не так при оплате, напиши в личные сообщения @tovanova_n");
    private final String message;
}
