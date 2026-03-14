package com.team2.fabackend.domain.user;

import com.team2.fabackend.global.enums.SocialType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by their user ID and social login type.
     *
     * @param userId     The user's unique ID.
     * @param socialType The social login type.
     * @return An Optional containing the User if found.
     */
    Optional<User> findByUserIdAndSocialType(String userId, SocialType socialType);

    /**
     * Retrieves a paged list of all users.
     *
     * @param pageable Pagination information.
     * @return A Page of User entities.
     */
    @NotNull Page<User> findAll(@NotNull Pageable pageable);

    /**
     * Checks if a user exists with the given user ID and social login type.
     *
     * @param userId     The user's unique ID.
     * @param socialType The social login type.
     * @return True if the user exists, false otherwise.
     */
    boolean existsByUserIdAndSocialType(String userId, SocialType socialType);

    /**
     * Checks if a user exists with the given phone number.
     *
     * @param phoneNumber The phone number to check.
     * @return True if a user exists with this phone number, false otherwise.
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Finds a user by their phone number and social login type.
     *
     * @param phoneNumber The phone number.
     * @param socialType  The social login type.
     * @return An Optional containing the User if found.
     */
    Optional<User> findByPhoneNumberAndSocialType(String phoneNumber, SocialType socialType);

    /**
     * Checks if a user exists with the given phone number and social login type.
     *
     * @param phoneNumber The phone number.
     * @param socialType  The social login type.
     * @return True if the user exists, false otherwise.
     */
    boolean existsByPhoneNumberAndSocialType(String phoneNumber, SocialType socialType);

    /**
     * Finds a user by their user ID, phone number, and social login type.
     *
     * @param userId      The user's ID.
     * @param phoneNumber The phone number.
     * @param socialType  The social login type.
     * @return An Optional containing the User if found.
     */
    Optional<User> findByUserIdAndPhoneNumberAndSocialType(String userId, String phoneNumber, SocialType socialType);

    /**
     * Finds a user by their user ID and phone number.
     *
     * @param userId      The user's ID.
     * @param phoneNumber The phone number.
     * @return An Optional containing the User if found.
     */
    Optional<User> findByUserIdAndPhoneNumber(String userId, String phoneNumber);
}
