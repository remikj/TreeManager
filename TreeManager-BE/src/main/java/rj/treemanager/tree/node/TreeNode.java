package rj.treemanager.tree.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "parentNode")
public class TreeNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(AddUpdateView.class)
    private long value;

    private long sumToRoot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore // ignore to not create infinitely referencing objects for serialization
    private TreeNode parentNode;

    @OneToMany(mappedBy = "parentNode", fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade(CascadeType.REMOVE)
    @JsonView(AddWithChildrenView.class)
    private List<TreeNode> children;

    public TreeNode(long value) {
        this.value = value;
    }

    public TreeNode(long value, List<TreeNode> children) {
        this.value = value;
        this.children = children;
    }

    public TreeNode(TreeNode treeNode) {
        this.value = treeNode.getValue();
        if (treeNode.getChildren() == null) {
            return;
        }
        this.children = treeNode.getChildren().stream()
                .map(TreeNode::new)
                .collect(Collectors.toList());
    }

    public static class AddUpdateView {
    }

    public static class AddWithChildrenView extends AddUpdateView {
    }
}
