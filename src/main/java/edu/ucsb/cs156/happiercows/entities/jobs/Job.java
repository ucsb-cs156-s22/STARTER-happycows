package edu.ucsb.cs156.happiercows.entities.jobs;

import edu.ucsb.cs156.happiercows.entities.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity(name = "jobs")
@EntityListeners(AuditingEntityListener.class)

public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonSerialize(using = UserSerializer.class, as=Job.UserSummary.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String status;
    @Builder.Default
    private String log = "";

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserSummary {
        private long id;
        private String email;
        private String fullName;
        private boolean admin;

        public static UserSummary toUserSummary(User user) {
            return UserSummary.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .admin(user.isAdmin())
                .build();
        }
    }

    public static class UserSerializer extends JsonSerializer<User> {

        @Override
        public void serialize(User user,
                JsonGenerator jsonGenerator,
                SerializerProvider serializerProvider)
                throws IOException, JsonProcessingException {
            
            jsonGenerator.writeObject(UserSummary.toUserSummary(user));
        }
    }
}
