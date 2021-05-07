package com.future.webmagic.task;

import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

@Component
public class MotoPipeline implements Pipeline {
    @Override
    public void process(ResultItems resultItems, Task task) {
        String content = resultItems.get("");
    }
}
