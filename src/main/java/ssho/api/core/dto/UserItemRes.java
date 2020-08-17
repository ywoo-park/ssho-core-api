package ssho.api.core.dto;

import lombok.Data;
import ssho.api.core.domain.item.Item;

import java.util.List;

@Data
public class UserItemRes {
    private String userId;
    private List<Item> itemList;
}
