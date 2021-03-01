package ssho.api.core.domain.useritemcache.model;

import lombok.Data;
import ssho.api.core.domain.item.Item;

@Data
public class UserItem implements Comparable<UserItem> {
    private Item item;
    private Double rate;

    @Override
    public int compareTo(UserItem userItem) {
        if(this.getRate() < userItem.getRate()){
            return 1;
        }
        else if(this.getRate().equals(userItem.getRate())){
            return 0;
        }
        return -1;
    }
}
