package springboot.study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.study.domain.Article;
import springboot.study.dto.AddArticleRequest;
import springboot.study.dto.ArticleResponse;
import springboot.study.dto.UpdateArticleRequest;
import springboot.study.service.BlogService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BlogApiController {
    private final BlogService blogService;

    @PostMapping("/api/articles") //글 추가
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest addArticleRequest){
        Article savedArticle=blogService.save(addArticleRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle); //성공시 201 응답 코드 반환
    }

    @GetMapping("/api/articles") //글 전체 조회
    public ResponseEntity<List<ArticleResponse>> findAllArticles(){
        List<ArticleResponse> articles=blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(articles);
    }

    @GetMapping("api/articles/{id}") //url 경로에서 id값 추출
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id){
        Article article=blogService.findById(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id){ //url에서 {id}에 해당하는 값이 PathVariable을 통해 들어옴
        blogService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody UpdateArticleRequest request){
        Article updatedArticle=blogService.update(id, request);

        return ResponseEntity.ok()
                .body(updatedArticle);
    }

}