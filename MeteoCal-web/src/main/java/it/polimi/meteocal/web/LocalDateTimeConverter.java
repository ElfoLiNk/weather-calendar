package it.polimi.meteocal.web;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@FacesConverter("localDateTimeConverter")
public class LocalDateTimeConverter implements Converter<LocalDateTime> {

    @Override
    public LocalDateTime getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) return null;
        String pattern = (String) component.getAttributes().get("pattern");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern != null ? pattern : "yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(value, formatter);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, LocalDateTime value) {
        if (value == null) return "";
        String pattern = (String) component.getAttributes().get("pattern");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern != null ? pattern : "yyyy-MM-dd HH:mm");
        return value.format(formatter);
    }
}
