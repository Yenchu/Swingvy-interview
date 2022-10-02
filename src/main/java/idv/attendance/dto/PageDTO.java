package idv.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDTO<T> {
    private int page;
    private int size;
    private long total;
    private List<T> content;
}
