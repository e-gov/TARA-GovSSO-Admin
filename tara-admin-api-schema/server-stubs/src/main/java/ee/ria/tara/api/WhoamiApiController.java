package ee.ria.tara.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;

@Controller
@RequestMapping("${openapi.tARAGOVSSOAdmin.base-path:/edurdo/admin/1.0.0}")
public class WhoamiApiController implements WhoamiApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public WhoamiApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
