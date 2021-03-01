package ssho.api.core.service.landing;

import ssho.api.core.domain.landing.res.LandingRes;

public interface LandingService {
    LandingRes getLandingByUserId(int userId);
}
