package com.example.service;

import com.example.model.Course;
import com.example.model.User;
import com.example.repository.CourseRepository;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static com.example.textconstants.Constants.STATUS_OPEN;

@Service
@AllArgsConstructor
public class PaginationService {
    private CourseRepository courseRepository;
    private UserRepository userRepository;

    /**
     * @param page current page
     * @param size numbero of pages
     * @param theme theme if present
     * @param teacherId teacher if present
     * @param sortWay sort way if present
     * @param order order if present
     * @return  courses by page using parameters you entered
     */
    public Page<Course> findCoursesByParameters(Integer page, Integer size, String theme, String teacherId, String sortWay, String order) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (sortWay.equals("DATEDIFF(end_date, start_date)")) {
            if (order.equals("descending")) {
                return courseRepository.findCoursesByDurationDESC(STATUS_OPEN, pageable);
            }
            return courseRepository.findCoursesByDurationASC(STATUS_OPEN, pageable);
        }

        if (sortWay.equals("student_enrolled")) {
            if (order.equals("descending")) {
                return courseRepository.findCoursesByStudentEnrolledDESC(STATUS_OPEN, pageable);
            }
            return courseRepository.findCoursesByStudentEnrolledASC(STATUS_OPEN, pageable);
        }

        String sort = findSortWay(sortWay);
        Sort.Direction sortDirection = findSortDirection(order);
        pageable = PageRequest.of(page - 1, size, sortDirection, sort);

        if (theme.equals("") && teacherId.equals("")) {
            return courseRepository.findByCourseStatus(STATUS_OPEN, pageable);
        }
        if (!theme.equals("") && teacherId.equals("")) {
            return courseRepository.findByCourseStatusAndTheme(STATUS_OPEN, theme, pageable);
        }

        User teacher = userRepository.findUserById(Long.valueOf(teacherId));
        if (theme.equals("")) {
            return courseRepository.findByCourseStatusAndIdLecturer(STATUS_OPEN, teacher, pageable);
        }
        return courseRepository.findByCourseStatusAndIdLecturerAndTheme(STATUS_OPEN, teacher, theme, pageable);
    }

    private Sort.Direction findSortDirection(String order) {
        if (order != null && order.equals("descending")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    /**
     * @param sortWay way of sorting
     * @return string for pagination value
     */
    private String findSortWay(String sortWay) {
        if (sortWay != null && sortWay.equals("name")) {
            return "name";
        }
        return "id";
    }
}
