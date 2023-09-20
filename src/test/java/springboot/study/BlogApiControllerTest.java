package springboot.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import springboot.study.domain.Article;
import springboot.study.dto.AddArticleRequest;
import springboot.study.dto.UpdateArticleRequest;
import springboot.study.repository.BlogRepository;

import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper; //직렬화, 역직렬화를 위한 클래스

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc= MockMvcBuilders.webAppContextSetup(context)
                .build();
        blogRepository.deleteAll();
    }

    @DisplayName("addArticle: 블로그 글 추가에 성공")
    @Test
    public void addArticle() throws Exception{
        //given-블로그 글 추가에 필요한 요청 객체 생성
        final String url="/api/articles";
        final String title="title";
        final String content="content";
        final AddArticleRequest userRequest=new AddArticleRequest(title,content);
        final String requestBody=objectMapper.writeValueAsString(userRequest);

        //when-api에 json타입으로 요청
        ResultActions result=mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        //then-글 개수가 1인지 확인하고 저장된 데이터 확인
        result.andExpect(status().isCreated());

        List<Article> articles=blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1); //크기가 1인지 검증
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("findAllArticles: 블로그 글 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception{

        //given-블로그 글 저장
        final String url="/api/articles";
        final String title="title";
        final String content="content";

        blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        //when-목록 조회 API를 호출
        final ResultActions resultActions=mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        //then-반환받은 값 중 0번째 요소의 title, content를 확인
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(content))
                .andExpect(jsonPath("$[0].title").value(title));
    }

    @DisplayName("findArticle: 블로그 글 조회에 성공")
    @Test
    public void findArticle() throws Exception{
        //given-블로그 글 저장
        final String url="/api/articles/{id}";
        final String title="title";
        final String content="content";

        Article savedArticle=blogRepository.save(Article.builder()
                        .title(title)
                        .content(content)
                        .build());

        //when-저장한 블로그 글의 id값으로 API호출
        final ResultActions resultActions=mockMvc.perform(get(url,savedArticle.getId()));

        //then-반환받은 content와 title이 저장된 값과 같은지 확인
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.title").value(title));
    }

    @DisplayName("deleteArticle: 블로그 글 삭제 성공")
    @Test
    public void deleteArticle() throws Exception{
        //given-블로그 글 저장
        final String url="/api/articles/{id}";
        final String title="title";
        final String content="content";

        Article savedArticle=blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        //when-저장한 블로그 글의 id값으로 삭제API호출
        mockMvc.perform(delete(url,savedArticle.getId()))
                .andExpect(status().isOk());

        //then-블로그 글 리스트를 전체 조회해서 배열 크기가 0인지 확인
        List<Article> articles=blogRepository.findAll();

        assertThat(articles).isEmpty();
    }

    @DisplayName("updateArticle: 블로그 글 수정 성공")
    @Test
    public void updateArticle() throws Exception{
        //given-블로그 글 저장, 수정에 필요한 요청 객체 생성
        final String url="/api/articles/{id}";
        final String title="title";
        final String content="content";

        Article savedArticle=blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        final String newTitle="new Title";
        final String newContent = "new Content";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        //when-api로 수정 요청 json타입으로 보냄
        ResultActions result=mockMvc.perform(put(url,savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        //then-id로 조회 후 수정 확인
        result.andExpect(status().isOk());

        Article article=blogRepository.findById(savedArticle.getId()).get();
        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);
    }


}