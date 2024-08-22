package com.github.explorer.repo;

import com.github.explorer.module.Github;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GithubRepository extends JpaRepository<Github, String> {
}