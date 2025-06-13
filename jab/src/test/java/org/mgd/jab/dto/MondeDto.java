package org.mgd.jab.dto;

import java.util.List;

public class MondeDto extends Dto {
    private List<PaysDto> payss;

    public List<PaysDto> getPayss() {
        return payss;
    }

    public void setPayss(List<PaysDto> payss) {
        this.payss = payss;
    }
}
