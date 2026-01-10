package sample.cafekiosk.learning;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/* # 학습 테스트
 * 학습하기 위해서 테스트를 도구로 사용하는 방법
 * 잘 모르는 기능, 라이브러리, 프레임워크를 학습하기 위해 작성하는 테스트
 * 여러 테스트 케이스를 스스로 정의하고 검증하는 과정을 통해  보다 구체적인 동작과 기능을 학습을 할 수 있음
 */

public class GuavaLearningTest {

    @Test
    @DisplayName("partition 메서드는 List를 쪼개서 List로 넣어준다 - 1")
    void testPartition1() {
        // given
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6);

        // when
        List<List<Integer>> partition = Lists.partition(integers, 3);

        // then
        assertThat(partition).hasSize(2)
                .isEqualTo(
                        List.of(List.of(1,2,3),
                                List.of(4,5,6))
                );
    }

    @Test
    @DisplayName("partition 메서드는 List를 쪼개서 List로 넣어준다 - 2")
    void testPartition2() {
        // given
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6);

        // when
        List<List<Integer>> partition = Lists.partition(integers, 4);

        // then
        assertThat(partition).hasSize(2)
                .isEqualTo(
                        List.of(List.of(1,2,3,4),
                                List.of(5,6))
                );
    }


    @Test
    @DisplayName("multimap은 하나의 키에 여러 value(Collection)를 넣을 수 있다.")
    void test1() {
        // given
        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("a", "a1");
        multimap.put("a", "a2");
        multimap.put("a", "a3");
        multimap.put("b", "b1");
        multimap.put("b", "b2");
        multimap.put("c", "c1");

        // when
        Collection<String> strings = multimap.get("a");

        // then
        assertThat(strings).hasSize(3)
                .isEqualTo(
                        List.of("a1", "a2", "a3")
                );
    }


    @TestFactory
    @DisplayName("multimap 삭제테스트")
    Collection<DynamicTest> test2() {
        // given
        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("a", "a1");
        multimap.put("a", "a2");
        multimap.put("a", "a3");
        multimap.put("b", "b1");
        multimap.put("b", "b2");
        multimap.put("c", "c1");

        return List.of(
                DynamicTest.dynamicTest("1개 value 삭제", () -> {
                    // when
                    multimap.remove("a", "a1");

                    // then
                    assertThat(multimap.get("a")).hasSize(2)
                            .contains("a2", "a3");
                }),
                DynamicTest.dynamicTest("1개 키를 삭제 : Null이 아닌 Empty", () -> {
                    // when
                    multimap.removeAll("a");

//                    assertThat(multimap.get("a")).isNull();
                    assertThat(multimap.get("a")).isEmpty();
                    assertThat(multimap.size()).isEqualTo(3);
                })
        );


    }
    

}
