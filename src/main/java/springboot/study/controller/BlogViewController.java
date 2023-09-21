package springboot.study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import springboot.study.domain.Article;
import springboot.study.dto.ArticleListViewResponse;
import springboot.study.dto.ArticleViewResponse;
import springboot.study.service.BlogService;
import org.springframework.ui.Model;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController { //블로그 글 전체 리스트를 담는 뷰 컨트롤러
    private final BlogService blogService;

    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleListViewResponse> articles = blogService.findAll().stream()
                .map(ArticleListViewResponse::new)
                .toList();
        model.addAttribute("articles", articles);

        return "articleList";
    }

    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        Article article = blogService.findById(id);
        model.addAttribute("article", new ArticleViewResponse(article));

        return "article";
    }

    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false) Long id, Model model) { //글 생성과 수정은 같은 화면으로 나타나지만 단순 글 생성의 경우 id를 파라미터로 받을 필요 없이 글 작성하면 되고, 수정인 경우 id를 받아서 수정한다는 의미로 request=false.
        if(id==null) { //글을 생성하여 작성하는 경우
            model.addAttribute("article",new ArticleViewResponse()); //글을 새로 작성하는 경우 빈 객체를 만듦
        } else { //글을 수정하는 경우
            Article article=blogService.findById(id);
            model.addAttribute("article",new ArticleViewResponse(article)); //기존의 글(객체)을 보여줌
        }
        return "newArticle";
    }
}