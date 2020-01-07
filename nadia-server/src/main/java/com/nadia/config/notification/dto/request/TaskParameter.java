package com.nadia.config.notification.dto.request;

import com.google.common.collect.Lists;
import com.nadia.config.notification.enums.Event;
import lombok.Data;

import java.util.List;

@Data
public class TaskParameter {

    private Action action;

    public TaskParameter(Action action) {
        if (action instanceof Addition) {
            action.setEvent(Event.ADD);
        } else if (action instanceof Modification) {
            action.setEvent(Event.MODIFY);
        } else if (action instanceof Deletion) {
            action.setEvent(Event.DELETE);
        }
        this.action = action;
    }


    public interface Action {
        Event getEvent();

        void setEvent(Event event);
    }

    @Data
    public static class Addition implements Action {

        public Addition() {

        }

        public Addition(List<AdditionItem> items) {
            this.items = items;
        }

        public Addition add(AdditionItem item) {
            if (this.items == null) {
                this.items = Lists.newArrayList();
            }
            this.items.add(item);
            return this;
        }

        private List<AdditionItem> items;

        private Event event;

    }

    @Data
    public static class AdditionItem {

        public AdditionItem(Long configId, String value) {
            this.configId = configId;
            this.value = value;
        }

        private Long configId;

        private String value;

    }

    @Data
    public static class Modification implements Action {

        public Modification(Long configId, String before, String after) {
            this.configId = configId;
            this.before = before;
            this.after = after;
        }

        private Long configId;

        private String before;

        private String after;

        private Event event;

    }

    @Data
    public static class Deletion implements Action {

        public Deletion(List<Long> ids) {
            this.ids = ids;
        }

        private List<Long> ids;

        private Event event;
    }

}
