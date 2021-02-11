package ssho.api.core.domain.useritemcache.model;

import lombok.Data;
import ssho.api.core.domain.mall.model.Mall;

@Data
public class UserMall implements Comparable<UserMall> {
    private Mall mall;
    private Double rate;

    @Override
    public int compareTo(UserMall userMall) {
        if(this.getRate() < userMall.getRate()){
            return 1;
        }
        else if(this.getRate().equals(userMall.getRate())){
            return 0;
        }
        return -1;
    }
}
