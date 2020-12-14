package ru.DVorobiev.model;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DataSignal {
    private Date date;
    private int id_Node;
    private int id_Obj;
    private Double d_value;
    private Double d_valueSource;

    /**
     * Дату присваеваем автоматически
     * @param id_Node: идентификатор узла
     * @param id_Obj: идентификатор объекта
     * @param d_value : значение от процесса
     */
    public DataSignal(int id_Node, int id_Obj, Double d_value, Double d_valueSource) {
        this.id_Node=id_Node;
        this.id_Obj=id_Obj;
        this.date = new Date();
        this.d_value = d_value;
        this.d_valueSource=d_valueSource;
    }

    public int getId_Node() {
        return id_Node;
    }

    public void setId_Node(int id_Node) {
        this.id_Node = id_Node;
    }

    public int getId_Obj() {
        return id_Obj;
    }

    public void setId_Obj(int id_Obj) {
        this.id_Obj = id_Obj;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public Double getD_value() {
        return d_value;
    }

    public void setD_value(Double d_value) {
        this.d_value = d_value;
    }

    public Double getD_valueSource() {
        return d_valueSource;
    }

    public void setD_valueSource(Double d_valueSource) {
        this.d_valueSource = d_valueSource;
    }

    /**
     * метод который работает по умолчанию, автоматически присваивает текущую дату
     * @return String: строка дата время с учетом Locale
     */
    public String getStringDate() {
        String s_date;
        Locale locale = new Locale("ru", "RU");
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
        dateFormatSymbols.setWeekdays(new String[]{
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
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(pattern, dateFormatSymbols);
        s_date = simpleDateFormat.format(new Date());
        return s_date;
    }

    /**
     * метод который преобразовывает дату и время с учетом Locale
     * @return String: строка дата время с учетом Locale
     */
    public String getStringDate(Date date) {
        String s_date;
        Locale locale = new Locale("ru", "RU");
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
        dateFormatSymbols.setWeekdays(new String[]{
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
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(pattern, dateFormatSymbols);
        s_date = simpleDateFormat.format(date);
        return s_date;
    }
}
