function init() {
    const $ = go.GraphObject.make;
    novelDiv =
        $(go.Diagram, "novelDiv",
            {
                allowCopy: false,
                "draggingTool.dragsTree": true,
                layout:
                    $(go.LayeredDigraphLayout,
                        {}),
                "undoManager.isEnabled": true
            });
    novelDiv.addDiagramListener("Modified", e => {
        var button = document.getElementById("SaveButton");
        if (button) button.disabled = !novelDiv.isModified;
        var idx = document.title.indexOf("*");
        if (novelDiv.isModified) {
            if (idx < 0) document.title += "*";
        } else {
            if (idx >= 0) document.title = document.title.slice(0, idx);
        }
    });

    var pinkGrad = $(go.Brush, "Linear", { 0: "#df39fb", 1: "#bb39fb" });
    var greengrad = $(go.Brush, "Linear", { 0: "#B1E2A5", 1: "#7AE060" });
    var actionTemplate =
        $(go.Panel, "Horizontal",
            $(go.TextBlock,
                { font: "bold 10pt Verdana, sans-serif", margin:4 },
                new go.Binding("text", "id")
            ),
            $(go.TextBlock,
                { font: "10pt Verdana, sans-serif", margin:4 },
                new go.Binding("text")
            )
        );
    novelDiv.nodeTemplate =
        $(go.Node, "Vertical",
            new go.Binding("isTreeExpanded").makeTwoWay(),
            new go.Binding("wasTreeExpanded").makeTwoWay(),
            { selectionObjectName: "BODY" },
            $(go.Panel, "Auto",
                { name: "BODY" },
                $(go.Shape, "RoundedRectangle",
                    { fill: pinkGrad, stroke: null }
                ),
                $(go.Panel, "Vertical",
                    { margin: 3 },
                    $("HyperlinkText",
                        node => window.location.href + node.data.url,
                        node => node.data.title,
                        { stretch: go.GraphObject.Horizontal,
                            font: "bold 12pt Verdana, sans-serif" }
                    ),
                    $(go.Panel, "Vertical",
                        { stretch: go.GraphObject.Horizontal, visible: false },
                        new go.Binding("visible", "choices", acts => (Array.isArray(acts) && acts.length > 0)),
                        $(go.Panel, "Table",
                            { stretch: go.GraphObject.Horizontal },
                            $(go.TextBlock, "Варианты",
                                {
                                    alignment: go.Spot.Left,
                                    font: "10pt Verdana, sans-serif"
                                }
                            ),
                            $("PanelExpanderButton", "COLLAPSIBLE",
                                { column: 1, alignment: go.Spot.Right }
                            )
                        ),
                        $(go.Panel, "Vertical",
                            {
                                name: "COLLAPSIBLE",
                                padding: 2,
                                stretch: go.GraphObject.Horizontal,
                                background: "#f3dcff",
                                defaultAlignment: go.Spot.Left,
                                itemTemplate: actionTemplate
                            },
                            new go.Binding("itemArray", "choices")
                        )
                    )
                )
            )
        );

    novelDiv.linkTemplate =
        $(go.Link, go.Link.Orthogonal,
            { deletable: false, corner: 10 },
            $(go.Shape,
                { strokeWidth: 2 }
            ),
            $(go.TextBlock, go.Link.OrientUpright,
                {
                    background: "white",
                    visible: false,
                    segmentOrientation: go.Link.None
                },
                new go.Binding("text", "choice"),
                new go.Binding("visible", "choice", a => a ? true : false)
            )
        );
    novelDiv.model = new go.GraphLinksModel(
        {
            copiesArrays: true,
            copiesArrayObjects: true,
            nodeDataArray: nodeDataArray,
            linkDataArray: linkDataArray
        });
}
window.addEventListener('DOMContentLoaded', init);