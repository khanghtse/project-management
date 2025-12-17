package khanghtse.com.projectmanagement.security.oauth2;

import jakarta.transaction.Transactional;
import khanghtse.com.projectmanagement.entities.AuthProvider;
import khanghtse.com.projectmanagement.entities.User;
import khanghtse.com.projectmanagement.repositories.AuthProviderRepository;
import khanghtse.com.projectmanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final AuthProviderRepository authProviderRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            // Lấy provider hiện tại (google, github...)
            String provider = userRequest.getClientRegistration().getRegistrationId();
            return processOAuth2User(oAuth2User, provider);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2User oAuth2User, String provider) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String avatarUrl = oAuth2User.getAttribute("picture");
        String providerId = oAuth2User.getAttribute("sub"); // Google ID

        if (email == null) {
            throw new RuntimeException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Cập nhật thông tin nếu user thay đổi bên Google
            user.setName(name);
            user.setAvatarUrl(avatarUrl);
            user = userRepository.save(user);
        } else {
            // Register new user
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setAvatarUrl(avatarUrl);
            user = userRepository.save(user);
        }

        // --- LOGIC MỚI: Check & Save AuthProvider ---
        // Kiểm tra xem user này đã link với provider này chưa?
        Optional<AuthProvider> authProviderOptional = authProviderRepository.findByProviderAndProviderId(provider, providerId);

        if (authProviderOptional.isEmpty()) {
            AuthProvider authProvider = new AuthProvider();
            authProvider.setUser(user);
            authProvider.setProvider(provider);
            authProvider.setProviderId(providerId);
            authProviderRepository.save(authProvider);
        }

        return oAuth2User;
    }
}
