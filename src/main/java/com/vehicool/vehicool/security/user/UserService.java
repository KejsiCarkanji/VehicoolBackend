package com.vehicool.vehicool.security.user;

import com.querydsl.core.types.Predicate;
import com.vehicool.vehicool.business.querydsl.UserFilter;
import com.vehicool.vehicool.business.querydsl.UserQueryDsl;
import com.vehicool.vehicool.business.querydsl.VehicleFilter;
import com.vehicool.vehicool.persistence.entity.ConfidentialFile;
import com.vehicool.vehicool.persistence.repository.DatabaseStorageRepository;
import com.vehicool.vehicool.util.fileconfigs.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final DatabaseStorageRepository databaseStorageRepository;
    private final UserQueryDsl userQueryDsl;

    public User getUserByUsername(String username) {
        return repository.findByUsername(username).orElse(null);
    }

    public User updateUser(User user, String username) {
        user.setUsername(username);
        repository.saveAndFlush(user);
        return user;
    }
    public Page<User> listUsers(UserFilter userFilter, Pageable pageRequest) {
        Predicate filter = userQueryDsl.filter(userFilter);
        return repository.findAll(filter,pageRequest);
    }

    public User getUserById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }

    public List<byte[]> getUserConfidentialFiles(String username) {
        User user = repository.findByUsername(username).orElse(null);
        if (user == null) {
            return null;
        }
        List<byte[]> images = new ArrayList<>();
        List<ConfidentialFile> lenderConfidentialFiled = user.getConfidentialFiles();
        for (ConfidentialFile currentFile : lenderConfidentialFiled) {
            byte[] image = ImageUtils.decompressImage(currentFile.getImageData());
            images.add(image);
        }
        return images;
    }

    @Transactional
    public String uploadConfidentialFile(List<MultipartFile> file, String username) throws IOException {

        User user = repository.findByUsername(username).orElse(null);
        if (user == null) {
            return "USER NOT FOUND!";
        }
        List<ConfidentialFile> list = new ArrayList<>();
        for (MultipartFile current : file) {
            ConfidentialFile confidentialFile = ConfidentialFile.builder().name(current.getOriginalFilename()).type(current.getContentType()).user(user).imageData(ImageUtils.compressImage(current.getBytes())).build();
            list.add(confidentialFile);
        }
        databaseStorageRepository.saveAll(list);
        if (!list.isEmpty()) {
            return "file uploaded successfully !";
        }
        return null;
    }
}
