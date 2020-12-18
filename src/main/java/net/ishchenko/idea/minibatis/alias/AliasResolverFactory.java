package net.ishchenko.idea.minibatis.alias;


import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public class AliasResolverFactory {
    @NotNull
    public static AliasResolver createAnnotationResolver(@NotNull Project project) {
        return new AnnotationAliasResolver(project);
    }

}
