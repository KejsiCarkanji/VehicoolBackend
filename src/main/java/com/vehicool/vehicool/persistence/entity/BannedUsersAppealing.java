package com.vehicool.vehicool.persistence.entity;

import com.vehicool.vehicool.security.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "banned_users_appealing")
public class BannedUsersAppealing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="description",nullable = false)
    private String Description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "status")
    private DataPool status;

    @OneToMany(mappedBy ="userBanAppeal")
    private List<ConfidentialFile> confidentialFiles;


}
