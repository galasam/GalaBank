package unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import org.springframework.data.repository.CrudRepository;

public class RepositoryMockHelper {

  public static <T extends CrudRepository> T getEmptyRepository(Class<T> type) {
    T repository = mock(type);
    when(repository.findAll()).thenReturn(new ArrayList<>());
    return repository;
  }

}
