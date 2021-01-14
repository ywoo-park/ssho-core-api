package ssho.api.core.domain.mall.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum CategoryCode {
    TOP("TOP"),
    BOTTOM("BOTTOM"),
    SKIRT("SKIRT"),
    OUTER("OUTER"),
    DRESS("DRESS"),
    SHOES("SHOES"),
    HAT("HAT"),
    EXTRA("EXTRA");

    public String code;

    CategoryCode(){}
    CategoryCode(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}
