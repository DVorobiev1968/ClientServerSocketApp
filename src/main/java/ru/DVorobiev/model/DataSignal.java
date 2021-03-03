package ru.dvorobiev.model;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import lombok.Data;

@Data
public class DataSignal {
    private Date date;
    private long time;

    /** Идентификатор узла */
    private int nodeId;
    /** Идентификатор объекта */
    private int objectId;
    /** Значение от процесса */
    private Double value;
    /** */
    private Double valueSource;

    /**
     * Дату присваеваем автоматически
     *
     * @param nodeId: идентификатор узла
     * @param objectId: идентификатор объекта
     * @param value: значение от процесса
     * @param valueSource:
     */
    public DataSignal(int nodeId, int objectId, Double value, Double valueSource) {
        this.nodeId = nodeId;
        this.objectId = objectId;
        this.date = new Date();
        this.time = this.date.getTime();
        this.value = value;
        this.valueSource = valueSource;
    }

    /**
     * метод который работает по умолчанию, автоматически присваивает текущую дату
     *
     * @return String: строка дата время с учетом Locale
     */
    public String getDateString() {
        String dateStr;
        Locale locale = new Locale("ru", "RU");
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
        dateFormatSymbols.setWeekdays(
                new String[] {
                    "Не используется",
                    "Понедельник",
                    "Вторник",
                    "Среда",
                    "Четверг",
                    "Пятница",
                    "Суббота",
                    "Воскресенье"
                });

        String pattern = "dd/MM/yyyy HH:mm:ss.SSS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, dateFormatSymbols);
        dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    /**
     * метод который преобразовывает дату и время с учетом Locale
     *
     * @param date дата
     * @return String: строка дата время с учетом Locale
     */
    public String getDateString(Date date) {
        String dateStr, timeStr;
        String pattern;

        SimpleDateFormat simpleDateFormat;
        Locale locale = new Locale("ru", "RU");
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
        dateFormatSymbols.setWeekdays(
                new String[] {
                    "Не используется",
                    "Понедельник",
                    "Вторник",
                    "Среда",
                    "Четверг",
                    "Пятница",
                    "Суббота",
                    "Воскресенье"
                });

        pattern = "dd/MM/yyyy";
        simpleDateFormat = new SimpleDateFormat(pattern, dateFormatSymbols);
        dateStr = simpleDateFormat.format(date);
        this.setDate(new Date(dateStr));

        pattern = "dd/MM/yyyy HH:mm:ss.SSS";
        simpleDateFormat = new SimpleDateFormat(pattern, dateFormatSymbols);
        dateStr = simpleDateFormat.format(date);
        this.setTime(date.getTime());
        return dateStr;
    }
}
