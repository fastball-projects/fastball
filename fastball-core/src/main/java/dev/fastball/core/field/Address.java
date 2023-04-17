package dev.fastball.core.field;

import lombok.Data;

@Data
public class Address {
    private String provinceCode;

    private String cityCode;

    private String areaCode;

    private String streetCode;

    private String addressDetail;
}
