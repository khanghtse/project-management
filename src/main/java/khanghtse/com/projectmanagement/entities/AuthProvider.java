package khanghtse.com.projectmanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auth_providers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String provider; // "google", "github"

    @Column(nullable = false)
    private String providerId; // ID trả về từ Google (sub)

//    public AuthProvider(User user, String provider, String providerId) {
//        this.user = user;
//        this.provider = provider;
//        this.providerId = providerId;
//    }
}
