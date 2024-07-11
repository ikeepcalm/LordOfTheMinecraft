package dev.ua.ikeepcalm.optional.emporium.wrappers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemWrapper {
    private final String type;
    private final int pathway;
    private final int sequence;

    public ItemWrapper(String type, int pathway, int sequence) {
        this.type = type;
        this.pathway = pathway;
        this.sequence = sequence;
    }

}