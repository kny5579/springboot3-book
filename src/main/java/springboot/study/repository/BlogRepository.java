package springboot.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.study.domain.Article;

public interface BlogRepository extends JpaRepository<Article,Long> {
}
