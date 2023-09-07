package cn.com.tzy.springbootactiviti.config.custom;

import java.awt.*;
import java.io.InputStream;
import java.util.*;
import java.util.List;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.Association;
import org.activiti.bpmn.model.AssociationDirection;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.BusinessRuleTask;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.CompensateEventDefinition;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ErrorEventDefinition;
import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.EventDefinition;
import org.activiti.bpmn.model.EventGateway;
import org.activiti.bpmn.model.EventSubProcess;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowElementsContainer;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.InclusiveGateway;
import org.activiti.bpmn.model.IntermediateCatchEvent;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.ManualTask;
import org.activiti.bpmn.model.MessageEventDefinition;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.ReceiveTask;
import org.activiti.bpmn.model.ScriptTask;
import org.activiti.bpmn.model.SendTask;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.SignalEventDefinition;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.TextAnnotation;
import org.activiti.bpmn.model.ThrowEvent;
import org.activiti.bpmn.model.TimerEventDefinition;
import org.activiti.bpmn.model.UserTask;
import org.activiti.bpmn.model.Transaction;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.exception.ActivitiInterchangeInfoNotFoundException;
import org.activiti.image.exception.ActivitiImageException;

/**
 1. Class to generate an svg based the diagram interchange information in a
 2. BPMN 2.0 process.
 */
public class DefaultProcessDiagramGenerator implements ProcessDiagramGenerator {

    private static final String DEFAULT_ACTIVITY_FONT_NAME = "Arial";

    private static final String DEFAULT_LABEL_FONT_NAME = "Arial";

    private static final String DEFAULT_ANNOTATION_FONT_NAME = "Arial";

    private static final String DEFAULT_DIAGRAM_IMAGE_FILE_NAME = "/image/na.svg";

    protected Map<Class<? extends BaseElement>, DefaultProcessDiagramGenerator.ActivityDrawInstruction> activityDrawInstructions = new HashMap<Class<? extends BaseElement>, DefaultProcessDiagramGenerator.ActivityDrawInstruction>();

    protected Map<Class<? extends BaseElement>, DefaultProcessDiagramGenerator.ArtifactDrawInstruction> artifactDrawInstructions = new HashMap<Class<? extends BaseElement>, DefaultProcessDiagramGenerator.ArtifactDrawInstruction>();

    @Override
    public String getDefaultActivityFontName() {
        return DEFAULT_ACTIVITY_FONT_NAME;
    }

    @Override
    public String getDefaultLabelFontName() {
        return DEFAULT_LABEL_FONT_NAME;
    }

    @Override
    public String getDefaultAnnotationFontName() {
        return DEFAULT_ANNOTATION_FONT_NAME;
    }

    @Override
    public String getDefaultDiagramImageFileName() {
        return DEFAULT_DIAGRAM_IMAGE_FILE_NAME;
    }

    // The instructions on how to draw a certain construct is
    // created statically and stored in a map for performance.
    public DefaultProcessDiagramGenerator() {
        // start event
        activityDrawInstructions.put(StartEvent.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        StartEvent startEvent = (StartEvent) flowNode;
                        if (startEvent.getEventDefinitions() != null && !startEvent.getEventDefinitions().isEmpty()) {
                            EventDefinition eventDefinition = startEvent.getEventDefinitions().get(0);
                            if (eventDefinition instanceof TimerEventDefinition) {
                                processDiagramCanvas.drawTimerStartEvent(flowNode.getId(),
                                        graphicInfo);
                            } else if (eventDefinition instanceof ErrorEventDefinition) {
                                processDiagramCanvas.drawErrorStartEvent(flowNode.getId(),
                                        graphicInfo);
                            } else if (eventDefinition instanceof SignalEventDefinition) {
                                processDiagramCanvas.drawSignalStartEvent(flowNode.getId(),
                                        graphicInfo);
                            } else if (eventDefinition instanceof MessageEventDefinition) {
                                processDiagramCanvas.drawMessageStartEvent(flowNode.getId(),
                                        graphicInfo);
                            } else {
                                processDiagramCanvas.drawNoneStartEvent(flowNode.getId(),
                                        graphicInfo);
                            }
                        } else {
                            processDiagramCanvas.drawNoneStartEvent(flowNode.getId(),
                                    graphicInfo);
                        }
                    }
                });

        // signal catch
        activityDrawInstructions.put(IntermediateCatchEvent.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        IntermediateCatchEvent intermediateCatchEvent = (IntermediateCatchEvent) flowNode;
                        if (intermediateCatchEvent.getEventDefinitions() != null && !intermediateCatchEvent.getEventDefinitions()
                                .isEmpty()) {
                            if (intermediateCatchEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition) {
                                processDiagramCanvas.drawCatchingSignalEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo,
                                        true);
                            } else if (intermediateCatchEvent.getEventDefinitions().get(0) instanceof TimerEventDefinition) {
                                processDiagramCanvas.drawCatchingTimerEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo,
                                        true);
                            } else if (intermediateCatchEvent.getEventDefinitions().get(0) instanceof MessageEventDefinition) {
                                processDiagramCanvas.drawCatchingMessageEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo,
                                        true);
                            }
                        }
                    }
                });

        // signal throw
        activityDrawInstructions.put(ThrowEvent.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        ThrowEvent throwEvent = (ThrowEvent) flowNode;
                        if (throwEvent.getEventDefinitions() != null && !throwEvent.getEventDefinitions().isEmpty()) {
                            if (throwEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition) {
                                processDiagramCanvas.drawThrowingSignalEvent(flowNode.getId(),
                                        graphicInfo);
                            } else if (throwEvent.getEventDefinitions().get(0) instanceof CompensateEventDefinition) {
                                processDiagramCanvas.drawThrowingCompensateEvent(flowNode.getId(),
                                        graphicInfo);
                            } else {
                                processDiagramCanvas.drawThrowingNoneEvent(flowNode.getId(),
                                        graphicInfo);
                            }
                        } else {
                            processDiagramCanvas.drawThrowingNoneEvent(flowNode.getId(),
                                    graphicInfo);
                        }
                    }
                });

        // end event
        activityDrawInstructions.put(EndEvent.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        EndEvent endEvent = (EndEvent) flowNode;
                        if (endEvent.getEventDefinitions() != null && !endEvent.getEventDefinitions().isEmpty()) {
                            if (endEvent.getEventDefinitions().get(0) instanceof ErrorEventDefinition) {
                                processDiagramCanvas.drawErrorEndEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo);
                            } else {
                                processDiagramCanvas.drawNoneEndEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo);
                            }
                        } else {
                            processDiagramCanvas.drawNoneEndEvent(flowNode.getId(),
                                    flowNode.getName(),
                                    graphicInfo);
                        }
                    }
                });

        // task
        activityDrawInstructions.put(Task.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawTask(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // user task
        activityDrawInstructions.put(UserTask.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawUserTask(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // script task
        activityDrawInstructions.put(ScriptTask.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawScriptTask(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // service task
        activityDrawInstructions.put(ServiceTask.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        ServiceTask serviceTask = (ServiceTask) flowNode;
                        processDiagramCanvas.drawServiceTask(flowNode.getId(),
                                serviceTask.getName(),
                                graphicInfo);
                    }
                });

        // receive task
        activityDrawInstructions.put(ReceiveTask.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawReceiveTask(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // send task
        activityDrawInstructions.put(SendTask.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawSendTask(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // manual task
        activityDrawInstructions.put(ManualTask.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawManualTask(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // businessRuleTask task
        activityDrawInstructions.put(BusinessRuleTask.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawBusinessRuleTask(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // exclusive gateway
        activityDrawInstructions.put(ExclusiveGateway.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawExclusiveGateway(flowNode.getId(),
                                graphicInfo);
                    }
                });

        // inclusive gateway
        activityDrawInstructions.put(InclusiveGateway.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawInclusiveGateway(flowNode.getId(),
                                graphicInfo);
                    }
                });

        // parallel gateway
        activityDrawInstructions.put(ParallelGateway.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawParallelGateway(flowNode.getId(),
                                graphicInfo);
                    }
                });

        // event based gateway
        activityDrawInstructions.put(EventGateway.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawEventBasedGateway(flowNode.getId(),
                                graphicInfo);
                    }
                });

        // Boundary timer
        activityDrawInstructions.put(BoundaryEvent.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        BoundaryEvent boundaryEvent = (BoundaryEvent) flowNode;
                        if (boundaryEvent.getEventDefinitions() != null && !boundaryEvent.getEventDefinitions().isEmpty()) {
                            if (boundaryEvent.getEventDefinitions().get(0) instanceof TimerEventDefinition) {

                                processDiagramCanvas.drawCatchingTimerEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo,
                                        boundaryEvent.isCancelActivity());
                            } else if (boundaryEvent.getEventDefinitions().get(0) instanceof ErrorEventDefinition) {

                                processDiagramCanvas.drawCatchingErrorEvent(flowNode.getId(),
                                        graphicInfo,
                                        boundaryEvent.isCancelActivity());
                            } else if (boundaryEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition) {
                                processDiagramCanvas.drawCatchingSignalEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo,
                                        boundaryEvent.isCancelActivity());
                            } else if (boundaryEvent.getEventDefinitions().get(0) instanceof MessageEventDefinition) {
                                processDiagramCanvas.drawCatchingMessageEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo,
                                        boundaryEvent.isCancelActivity());
                            } else if (boundaryEvent.getEventDefinitions().get(0) instanceof CompensateEventDefinition) {
                                processDiagramCanvas.drawCatchingCompensateEvent(flowNode.getId(),
                                        graphicInfo,
                                        boundaryEvent.isCancelActivity());
                            }
                        }
                    }
                });

        // subprocess
        activityDrawInstructions.put(SubProcess.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        if (graphicInfo.getExpanded() != null && !graphicInfo.getExpanded()) {
                            processDiagramCanvas.drawCollapsedSubProcess(flowNode.getId(),
                                    flowNode.getName(),
                                    graphicInfo,
                                    false);
                        } else {
                            processDiagramCanvas.drawExpandedSubProcess(flowNode.getId(),
                                    flowNode.getName(),
                                    graphicInfo,
                                    SubProcess.class);
                        }
                    }
                });
        // transaction
        activityDrawInstructions.put(Transaction.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        if (graphicInfo.getExpanded() != null && !graphicInfo.getExpanded()) {
                            processDiagramCanvas.drawCollapsedSubProcess(flowNode.getId(),
                                    flowNode.getName(),
                                    graphicInfo,
                                    false);
                        } else {
                            processDiagramCanvas.drawExpandedSubProcess(flowNode.getId(),
                                    flowNode.getName(),
                                    graphicInfo,
                                    Transaction.class);
                        }
                    }
                });

        // Event subprocess
        activityDrawInstructions.put(EventSubProcess.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        if (graphicInfo.getExpanded() != null && !graphicInfo.getExpanded()) {
                            processDiagramCanvas.drawCollapsedSubProcess(flowNode.getId(),
                                    flowNode.getName(),
                                    graphicInfo,
                                    true);
                        } else {
                            processDiagramCanvas.drawExpandedSubProcess(flowNode.getId(),
                                    flowNode.getName(),
                                    graphicInfo,
                                    EventSubProcess.class);
                        }
                    }
                });

        // call activity
        activityDrawInstructions.put(CallActivity.class,
                new DefaultProcessDiagramGenerator.ActivityDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawCollapsedCallActivity(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // text annotation
        artifactDrawInstructions.put(TextAnnotation.class,
                new DefaultProcessDiagramGenerator.ArtifactDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     Artifact artifact) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(artifact.getId());
                        TextAnnotation textAnnotation = (TextAnnotation) artifact;
                        processDiagramCanvas.drawTextAnnotation(textAnnotation.getId(),
                                textAnnotation.getText(),
                                graphicInfo);
                    }
                });

        // association
        artifactDrawInstructions.put(Association.class,
                new DefaultProcessDiagramGenerator.ArtifactDrawInstruction() {

                    @Override
                    public void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     Artifact artifact) {
                        Association association = (Association) artifact;
                        String sourceRef = association.getSourceRef();
                        String targetRef = association.getTargetRef();

                        // source and target can be instance of FlowElement or Artifact
                        BaseElement sourceElement = bpmnModel.getFlowElement(sourceRef);
                        BaseElement targetElement = bpmnModel.getFlowElement(targetRef);
                        if (sourceElement == null) {
                            sourceElement = bpmnModel.getArtifact(sourceRef);
                        }
                        if (targetElement == null) {
                            targetElement = bpmnModel.getArtifact(targetRef);
                        }
                        List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(artifact.getId());
                        graphicInfoList = connectionPerfectionizer(processDiagramCanvas,
                                bpmnModel,
                                sourceElement,
                                targetElement,
                                graphicInfoList);
                        int xPoints[] = new int[graphicInfoList.size()];
                        int yPoints[] = new int[graphicInfoList.size()];
                        for (int i = 1; i < graphicInfoList.size(); i++) {
                            GraphicInfo graphicInfo = graphicInfoList.get(i);
                            GraphicInfo previousGraphicInfo = graphicInfoList.get(i - 1);

                            if (i == 1) {
                                xPoints[0] = (int) previousGraphicInfo.getX();
                                yPoints[0] = (int) previousGraphicInfo.getY();
                            }
                            xPoints[i] = (int) graphicInfo.getX();
                            yPoints[i] = (int) graphicInfo.getY();
                        }

                        AssociationDirection associationDirection = association.getAssociationDirection();
                        processDiagramCanvas.drawAssociation(xPoints,
                                yPoints,
                                associationDirection,
                                false);
                    }
                });
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel,
                                       List<String> highLightedActivities,
                                       List<String> highLightedFlows,
                                       String activityFontName,
                                       String labelFontName,
                                       String annotationFontName) {
        return generateDiagram(bpmnModel,
                highLightedActivities,
                highLightedFlows,
                activityFontName,
                labelFontName,
                annotationFontName,
                false,
                null);
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel,
                                       List<String> highLightedActivities,
                                       List<String> highLightedFlows,
                                       String activityFontName,
                                       String labelFontName,
                                       String annotationFontName,
                                       boolean generateDefaultDiagram) {
        return generateDiagram(bpmnModel,
                highLightedActivities,
                highLightedFlows,
                activityFontName,
                labelFontName,
                annotationFontName,
                generateDefaultDiagram,
                null);
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel,
                                       List<String> highLightedActivities,
                                       List<String> highLightedFlows,
                                       String activityFontName,
                                       String labelFontName,
                                       String annotationFontName,
                                       boolean generateDefaultDiagram,
                                       String defaultDiagramImageFileName) {

        if (!bpmnModel.hasDiagramInterchangeInfo()) {
            if (!generateDefaultDiagram) {
                throw new ActivitiInterchangeInfoNotFoundException("No interchange information found.");
            }

            return getDefaultDiagram(defaultDiagramImageFileName);
        }

        return generateProcessDiagram(bpmnModel,
                highLightedActivities,
                highLightedFlows,
                activityFontName,
                labelFontName,
                annotationFontName).generateImage();
    }

    /**
     * Get default diagram image as bytes array
     * @return the default diagram image
     */
    protected InputStream getDefaultDiagram(String diagramImageFileName) {
        String imageFileName = diagramImageFileName != null ?
                diagramImageFileName :
                getDefaultDiagramImageFileName();
        InputStream imageStream = getClass().getResourceAsStream(imageFileName);
        if (imageStream == null) {
            throw new ActivitiImageException("Error occurred while getting default diagram image from file: " + imageFileName);
        }
        return imageStream;
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel,
                                       List<String> highLightedActivities,
                                       List<String> highLightedFlows) {
        return generateDiagram(bpmnModel,
                highLightedActivities,
                highLightedFlows,
                null,
                null,
                null,
                false,
                null);
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel,
                                       List<String> highLightedActivities) {
        return generateDiagram(bpmnModel,
                highLightedActivities,
                Collections.<String>emptyList());
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel,
                                       String activityFontName,
                                       String labelFontName,
                                       String annotationFontName) {

        return generateDiagram(bpmnModel,
                Collections.<String>emptyList(),
                Collections.<String>emptyList(),
                activityFontName,
                labelFontName,
                annotationFontName);
    }

    protected DefaultProcessDiagramCanvas generateProcessDiagram(BpmnModel bpmnModel,
                                                                                         List<String> highLightedActivities,
                                                                                         List<String> highLightedFlows,
                                                                                         String activityFontName,
                                                                                         String labelFontName,
                                                                                         String annotationFontName) {

        prepareBpmnModel(bpmnModel);

        DefaultProcessDiagramCanvas processDiagramCanvas = initProcessDiagramCanvas(bpmnModel,
                activityFontName,
                labelFontName,
                annotationFontName);

        // Draw pool shape, if process is participant in collaboration
        for (Pool pool : bpmnModel.getPools()) {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            processDiagramCanvas.drawPoolOrLane(pool.getId(),
                    pool.getName(),
                    graphicInfo);
        }

        // Draw lanes
        for (Process process : bpmnModel.getProcesses()) {
            for (Lane lane : process.getLanes()) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(lane.getId());
                processDiagramCanvas.drawPoolOrLane(lane.getId(),
                        lane.getName(),
                        graphicInfo);
            }
        }

        // Draw activities and their sequence-flows
        for (Process process : bpmnModel.getProcesses()) {
            for (FlowNode flowNode : process.findFlowElementsOfType(FlowNode.class)) {
                drawActivity(processDiagramCanvas,
                        bpmnModel,
                        flowNode,
                        highLightedActivities,
                        highLightedFlows);
            }
        }

        // Draw artifacts
        for (Process process : bpmnModel.getProcesses()) {

            for (Artifact artifact : process.getArtifacts()) {
                drawArtifact(processDiagramCanvas,
                        bpmnModel,
                        artifact);
            }

            List<SubProcess> subProcesses = process.findFlowElementsOfType(SubProcess.class,
                    true);
            if (subProcesses != null) {
                for (SubProcess subProcess : subProcesses) {
                    for (Artifact subProcessArtifact : subProcess.getArtifacts()) {
                        drawArtifact(processDiagramCanvas,
                                bpmnModel,
                                subProcessArtifact);
                    }
                }
            }
        }

        return processDiagramCanvas;
    }

    protected void prepareBpmnModel(BpmnModel bpmnModel) {

        // Need to make sure all elements have positive x and y.
        // Check all graphicInfo and update the elements accordingly

        List<GraphicInfo> allGraphicInfos = new ArrayList<GraphicInfo>();
        if (bpmnModel.getLocationMap() != null) {
            allGraphicInfos.addAll(bpmnModel.getLocationMap().values());
        }
        if (bpmnModel.getLabelLocationMap() != null) {
            allGraphicInfos.addAll(bpmnModel.getLabelLocationMap().values());
        }
        if (bpmnModel.getFlowLocationMap() != null) {
            for (List<GraphicInfo> flowGraphicInfos : bpmnModel.getFlowLocationMap().values()) {
                allGraphicInfos.addAll(flowGraphicInfos);
            }
        }

        if (allGraphicInfos.size() > 0) {

            boolean needsTranslationX = false;
            boolean needsTranslationY = false;

            double lowestX = 0.0;
            double lowestY = 0.0;

            // Collect lowest x and y
            for (GraphicInfo graphicInfo : allGraphicInfos) {

                double x = graphicInfo.getX();
                double y = graphicInfo.getY();

                if (x < lowestX) {
                    needsTranslationX = true;
                    lowestX = x;
                }
                if (y < lowestY) {
                    needsTranslationY = true;
                    lowestY = y;
                }
            }

            // Update all graphicInfo objects
            if (needsTranslationX || needsTranslationY) {

                double translationX = Math.abs(lowestX);
                double translationY = Math.abs(lowestY);

                for (GraphicInfo graphicInfo : allGraphicInfos) {
                    if (needsTranslationX) {
                        graphicInfo.setX(graphicInfo.getX() + translationX);
                    }
                    if (needsTranslationY) {
                        graphicInfo.setY(graphicInfo.getY() + translationY);
                    }
                }
            }
        }
    }

    protected void drawActivity(DefaultProcessDiagramCanvas processDiagramCanvas,
                                BpmnModel bpmnModel,
                                FlowNode flowNode,
                                List<String> highLightedActivities,
                                List<String> highLightedFlows) {

        DefaultProcessDiagramGenerator.ActivityDrawInstruction drawInstruction = activityDrawInstructions.get(flowNode.getClass());
        if (drawInstruction != null) {

            drawInstruction.draw(processDiagramCanvas,
                    bpmnModel,
                    flowNode);

            // Gather info on the multi instance marker
            boolean multiInstanceSequential = false;
            boolean multiInstanceParallel = false;
            boolean collapsed = false;
            if (flowNode instanceof Activity) {
                Activity activity = (Activity) flowNode;
                MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = activity.getLoopCharacteristics();
                if (multiInstanceLoopCharacteristics != null) {
                    multiInstanceSequential = multiInstanceLoopCharacteristics.isSequential();
                    multiInstanceParallel = !multiInstanceSequential;
                }
            }

            // Gather info on the collapsed marker
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            if (flowNode instanceof SubProcess) {
                collapsed = graphicInfo.getExpanded() != null && !graphicInfo.getExpanded();
            } else if (flowNode instanceof CallActivity) {
                collapsed = true;
            }

            // Actually draw the markers
            processDiagramCanvas.drawActivityMarkers((int) graphicInfo.getX(),
                    (int) graphicInfo.getY(),
                    (int) graphicInfo.getWidth(),
                    (int) graphicInfo.getHeight(),
                    multiInstanceSequential,
                    multiInstanceParallel,
                    collapsed);

            // Draw highlighted activities
            if (highLightedActivities.contains(flowNode.getId())) {
                drawHighLight(processDiagramCanvas,
                        bpmnModel.getGraphicInfo(flowNode.getId()));
            }
        }

        // Outgoing transitions of activity
        for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
            boolean highLighted = (highLightedFlows.contains(sequenceFlow.getId()));
            String defaultFlow = null;
            if (flowNode instanceof Activity) {
                defaultFlow = ((Activity) flowNode).getDefaultFlow();
            } else if (flowNode instanceof Gateway) {
                defaultFlow = ((Gateway) flowNode).getDefaultFlow();
            }

            boolean isDefault = false;
            if (defaultFlow != null && defaultFlow.equalsIgnoreCase(sequenceFlow.getId())) {
                isDefault = true;
            }
            boolean drawConditionalIndicator = sequenceFlow.getConditionExpression() != null && !(flowNode instanceof Gateway);

            String sourceRef = sequenceFlow.getSourceRef();
            String targetRef = sequenceFlow.getTargetRef();
            FlowElement sourceElement = bpmnModel.getFlowElement(sourceRef);
            FlowElement targetElement = bpmnModel.getFlowElement(targetRef);
            List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
            if (graphicInfoList != null && graphicInfoList.size() > 0) {
                graphicInfoList = connectionPerfectionizer(processDiagramCanvas,
                        bpmnModel,
                        sourceElement,
                        targetElement,
                        graphicInfoList);
                int xPoints[] = new int[graphicInfoList.size()];
                int yPoints[] = new int[graphicInfoList.size()];

                for (int i = 1; i < graphicInfoList.size(); i++) {
                    GraphicInfo graphicInfo = graphicInfoList.get(i);
                    GraphicInfo previousGraphicInfo = graphicInfoList.get(i - 1);

                    if (i == 1) {
                        xPoints[0] = (int) previousGraphicInfo.getX();
                        yPoints[0] = (int) previousGraphicInfo.getY();
                    }
                    xPoints[i] = (int) graphicInfo.getX();
                    yPoints[i] = (int) graphicInfo.getY();
                }

                processDiagramCanvas.drawSequenceflow(xPoints,
                        yPoints,
                        drawConditionalIndicator,
                        isDefault,
                        highLighted);

                // Draw sequenceflow label
                GraphicInfo labelGraphicInfo = bpmnModel.getLabelGraphicInfo(sequenceFlow.getId());
                if (labelGraphicInfo != null) {
                    processDiagramCanvas.drawLabel(sequenceFlow.getName(),
                            labelGraphicInfo,
                            false);
                }
            }
        }

        // Nested elements
        if (flowNode instanceof FlowElementsContainer) {
            for (FlowElement nestedFlowElement : ((FlowElementsContainer) flowNode).getFlowElements()) {
                if (nestedFlowElement instanceof FlowNode) {
                    drawActivity(processDiagramCanvas,
                            bpmnModel,
                            (FlowNode) nestedFlowElement,
                            highLightedActivities,
                            highLightedFlows);
                }
            }
        }
    }

    /**
     * This method makes coordinates of connection flow better.
     * @param processDiagramCanvas
     * @param bpmnModel
     * @param sourceElement
     * @param targetElement
     * @param graphicInfoList
     * @return
     */
    protected static List<GraphicInfo> connectionPerfectionizer(DefaultProcessDiagramCanvas processDiagramCanvas,
                                                                BpmnModel bpmnModel,
                                                                BaseElement sourceElement,
                                                                BaseElement targetElement,
                                                                List<GraphicInfo> graphicInfoList) {
        GraphicInfo sourceGraphicInfo = bpmnModel.getGraphicInfo(sourceElement.getId());
        GraphicInfo targetGraphicInfo = bpmnModel.getGraphicInfo(targetElement.getId());

        DefaultProcessDiagramCanvas.SHAPE_TYPE sourceShapeType = getShapeType(sourceElement);
        DefaultProcessDiagramCanvas.SHAPE_TYPE targetShapeType = getShapeType(targetElement);

        return processDiagramCanvas.connectionPerfectionizer(sourceShapeType,
                targetShapeType,
                sourceGraphicInfo,
                targetGraphicInfo,
                graphicInfoList);
    }

    /**
     * This method returns shape type of base element.<br>
     * Each element can be presented as rectangle, rhombus, or ellipse.
     * @param baseElement
     * @return DefaultProcessDiagramCanvas.SHAPE_TYPE
     */
    protected static DefaultProcessDiagramCanvas.SHAPE_TYPE getShapeType(BaseElement baseElement) {
        if (baseElement instanceof Task || baseElement instanceof Activity || baseElement instanceof TextAnnotation) {
            return DefaultProcessDiagramCanvas.SHAPE_TYPE.Rectangle;
        } else if (baseElement instanceof Gateway) {
            return DefaultProcessDiagramCanvas.SHAPE_TYPE.Rhombus;
        } else if (baseElement instanceof Event) {
            return DefaultProcessDiagramCanvas.SHAPE_TYPE.Ellipse;
        }
        // unknown source element, just do not correct coordinates
        return null;
    }

    protected static GraphicInfo getLineCenter(List<GraphicInfo> graphicInfoList) {
        GraphicInfo gi = new GraphicInfo();

        int xPoints[] = new int[graphicInfoList.size()];
        int yPoints[] = new int[graphicInfoList.size()];

        double length = 0;
        double[] lengths = new double[graphicInfoList.size()];
        lengths[0] = 0;
        double m;
        for (int i = 1; i < graphicInfoList.size(); i++) {
            GraphicInfo graphicInfo = graphicInfoList.get(i);
            GraphicInfo previousGraphicInfo = graphicInfoList.get(i - 1);

            if (i == 1) {
                xPoints[0] = (int) previousGraphicInfo.getX();
                yPoints[0] = (int) previousGraphicInfo.getY();
            }
            xPoints[i] = (int) graphicInfo.getX();
            yPoints[i] = (int) graphicInfo.getY();

            length += Math.sqrt(
                    Math.pow((int) graphicInfo.getX() - (int) previousGraphicInfo.getX(),
                            2) +
                            Math.pow((int) graphicInfo.getY() - (int) previousGraphicInfo.getY(),
                                    2)
            );
            lengths[i] = length;
        }
        m = length / 2;
        int p1 = 0;
        int p2 = 1;
        for (int i = 1; i < lengths.length; i++) {
            double len = lengths[i];
            p1 = i - 1;
            p2 = i;
            if (len > m) {
                break;
            }
        }

        GraphicInfo graphicInfo1 = graphicInfoList.get(p1);
        GraphicInfo graphicInfo2 = graphicInfoList.get(p2);

        double AB = (int) graphicInfo2.getX() - (int) graphicInfo1.getX();
        double OA = (int) graphicInfo2.getY() - (int) graphicInfo1.getY();
        double OB = lengths[p2] - lengths[p1];
        double ob = m - lengths[p1];
        double ab = AB * ob / OB;
        double oa = OA * ob / OB;

        double mx = graphicInfo1.getX() + ab;
        double my = graphicInfo1.getY() + oa;

        gi.setX(mx);
        gi.setY(my);
        return gi;
    }

    protected void drawArtifact(DefaultProcessDiagramCanvas processDiagramCanvas,
                                BpmnModel bpmnModel,
                                Artifact artifact) {

        DefaultProcessDiagramGenerator.ArtifactDrawInstruction drawInstruction = artifactDrawInstructions.get(artifact.getClass());
        if (drawInstruction != null) {
            drawInstruction.draw(processDiagramCanvas,
                    bpmnModel,
                    artifact);
        }
    }

    private static void drawHighLight(DefaultProcessDiagramCanvas processDiagramCanvas,
                                      GraphicInfo graphicInfo) {
        processDiagramCanvas.drawHighLight((int) graphicInfo.getX(),
                (int) graphicInfo.getY(),
                (int) graphicInfo.getWidth(),
                (int) graphicInfo.getHeight());
    }

    private static void drawHighLight(DefaultProcessDiagramCanvas processDiagramCanvas, GraphicInfo graphicInfo, Color color, FlowNode flowNode) {
        if(flowNode instanceof Event){
            processDiagramCanvas.drawHighLightEvent((int)graphicInfo.getX(), (int)graphicInfo.getY(), (int)graphicInfo.getWidth(), (int)graphicInfo.getHeight(),color);
        }else if(flowNode instanceof Gateway){
            processDiagramCanvas.drawHighLightGateway((int)graphicInfo.getX(), (int)graphicInfo.getY(), (int)graphicInfo.getWidth(), (int)graphicInfo.getHeight(),color);
        }else if(flowNode instanceof Task){
            processDiagramCanvas.drawHighLightTask((int)graphicInfo.getX(), (int)graphicInfo.getY(), (int)graphicInfo.getWidth(), (int)graphicInfo.getHeight(),color);
        }else if(flowNode instanceof SubProcess){
            processDiagramCanvas.drawHighLightSubProcess((int)graphicInfo.getX(), (int)graphicInfo.getY(), (int)graphicInfo.getWidth(), (int)graphicInfo.getHeight(),color);
        }else{
            processDiagramCanvas.drawHighLight((int)graphicInfo.getX(), (int)graphicInfo.getY(), (int)graphicInfo.getWidth(), (int)graphicInfo.getHeight(),color);
        }
    }

    protected static DefaultProcessDiagramCanvas initProcessDiagramCanvas(BpmnModel bpmnModel,
                                                                                                  String activityFontName,
                                                                                                  String labelFontName,
                                                                                                  String annotationFontName) {

        // We need to calculate maximum values to know how big the image will be in its entirety
        double minX = Double.MAX_VALUE;
        double maxX = 0;
        double minY = Double.MAX_VALUE;
        double maxY = 0;

        for (Pool pool : bpmnModel.getPools()) {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            minX = graphicInfo.getX();
            maxX = graphicInfo.getX() + graphicInfo.getWidth();
            minY = graphicInfo.getY();
            maxY = graphicInfo.getY() + graphicInfo.getHeight();
        }

        List<FlowNode> flowNodes = gatherAllFlowNodes(bpmnModel);
        for (FlowNode flowNode : flowNodes) {

            GraphicInfo flowNodeGraphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());

            if (flowNodeGraphicInfo == null) {
                continue;
            }

            // width
            if (flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth() > maxX) {
                maxX = flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth();
            }
            if (flowNodeGraphicInfo.getX() < minX) {
                minX = flowNodeGraphicInfo.getX();
            }
            // height
            if (flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight() > maxY) {
                maxY = flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight();
            }
            if (flowNodeGraphicInfo.getY() < minY) {
                minY = flowNodeGraphicInfo.getY();
            }

            for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
                List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
                if (graphicInfoList != null) {
                    for (GraphicInfo graphicInfo : graphicInfoList) {
                        // width
                        if (graphicInfo.getX() > maxX) {
                            maxX = graphicInfo.getX();
                        }
                        if (graphicInfo.getX() < minX) {
                            minX = graphicInfo.getX();
                        }
                        // height
                        if (graphicInfo.getY() > maxY) {
                            maxY = graphicInfo.getY();
                        }
                        if (graphicInfo.getY() < minY) {
                            minY = graphicInfo.getY();
                        }
                    }
                }
            }
        }

        List<Artifact> artifacts = gatherAllArtifacts(bpmnModel);
        for (Artifact artifact : artifacts) {

            GraphicInfo artifactGraphicInfo = bpmnModel.getGraphicInfo(artifact.getId());

            if (artifactGraphicInfo != null) {
                // width
                if (artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth() > maxX) {
                    maxX = artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth();
                }
                if (artifactGraphicInfo.getX() < minX) {
                    minX = artifactGraphicInfo.getX();
                }
                // height
                if (artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight() > maxY) {
                    maxY = artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight();
                }
                if (artifactGraphicInfo.getY() < minY) {
                    minY = artifactGraphicInfo.getY();
                }
            }

            List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(artifact.getId());
            if (graphicInfoList != null) {
                for (GraphicInfo graphicInfo : graphicInfoList) {
                    // width
                    if (graphicInfo.getX() > maxX) {
                        maxX = graphicInfo.getX();
                    }
                    if (graphicInfo.getX() < minX) {
                        minX = graphicInfo.getX();
                    }
                    // height
                    if (graphicInfo.getY() > maxY) {
                        maxY = graphicInfo.getY();
                    }
                    if (graphicInfo.getY() < minY) {
                        minY = graphicInfo.getY();
                    }
                }
            }
        }

        int nrOfLanes = 0;
        for (Process process : bpmnModel.getProcesses()) {
            for (Lane l : process.getLanes()) {

                nrOfLanes++;

                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(l.getId());
                if (graphicInfo != null) {
                    // width
                    if (graphicInfo.getX() + graphicInfo.getWidth() > maxX) {
                        maxX = graphicInfo.getX() + graphicInfo.getWidth();
                    }
                    if (graphicInfo.getX() < minX) {
                        minX = graphicInfo.getX();
                    }
                    // height
                    if (graphicInfo.getY() + graphicInfo.getHeight() > maxY) {
                        maxY = graphicInfo.getY() + graphicInfo.getHeight();
                    }
                    if (graphicInfo.getY() < minY) {
                        minY = graphicInfo.getY();
                    }
                }
            }
        }

        // Special case, see https://activiti.atlassian.net/browse/ACT-1431
        if (flowNodes.isEmpty() && bpmnModel.getPools().isEmpty() && nrOfLanes == 0) {
            // Nothing to show
            minX = 0;
            minY = 0;
        }

        return new DefaultProcessDiagramCanvas((int) maxX + 10,
                (int) maxY + 10,
                (int) minX,
                (int) minY,
                activityFontName,
                labelFontName,
                annotationFontName);
    }

    protected static List<Artifact> gatherAllArtifacts(BpmnModel bpmnModel) {
        List<Artifact> artifacts = new ArrayList<Artifact>();
        for (Process process : bpmnModel.getProcesses()) {
            artifacts.addAll(process.getArtifacts());
        }
        return artifacts;
    }

    protected static List<FlowNode> gatherAllFlowNodes(BpmnModel bpmnModel) {
        List<FlowNode> flowNodes = new ArrayList<FlowNode>();
        for (Process process : bpmnModel.getProcesses()) {
            flowNodes.addAll(gatherAllFlowNodes(process));
        }
        return flowNodes;
    }

    protected static List<FlowNode> gatherAllFlowNodes(FlowElementsContainer flowElementsContainer) {
        List<FlowNode> flowNodes = new ArrayList<FlowNode>();
        for (FlowElement flowElement : flowElementsContainer.getFlowElements()) {
            if (flowElement instanceof FlowNode) {
                flowNodes.add((FlowNode) flowElement);
            }
            if (flowElement instanceof FlowElementsContainer) {
                flowNodes.addAll(gatherAllFlowNodes((FlowElementsContainer) flowElement));
            }
        }
        return flowNodes;
    }

    public Map<Class<? extends BaseElement>, DefaultProcessDiagramGenerator.ActivityDrawInstruction> getActivityDrawInstructions() {
        return activityDrawInstructions;
    }

    public void setActivityDrawInstructions(
            Map<Class<? extends BaseElement>, DefaultProcessDiagramGenerator.ActivityDrawInstruction> activityDrawInstructions) {
        this.activityDrawInstructions = activityDrawInstructions;
    }

    public Map<Class<? extends BaseElement>, DefaultProcessDiagramGenerator.ArtifactDrawInstruction> getArtifactDrawInstructions() {
        return artifactDrawInstructions;
    }

    public void setArtifactDrawInstructions(
            Map<Class<? extends BaseElement>, DefaultProcessDiagramGenerator.ArtifactDrawInstruction> artifactDrawInstructions) {
        this.artifactDrawInstructions = artifactDrawInstructions;
    }

    protected interface ActivityDrawInstruction {

        void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                  BpmnModel bpmnModel,
                  FlowNode flowNode);
    }

    protected interface ArtifactDrawInstruction {

        void draw(DefaultProcessDiagramCanvas processDiagramCanvas,
                  BpmnModel bpmnModel,
                  Artifact artifact);
    }

    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType,List<String> highLightedFinishes, List<String> highLightedActivities, List<String> highLightedFlows, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader, double scaleFactor) {
        return this.generateProcessDiagram(bpmnModel, imageType,highLightedFinishes, highLightedActivities, highLightedFlows, activityFontName, labelFontName, annotationFontName, customClassLoader, scaleFactor).generateImage();
    }

    protected DefaultProcessDiagramCanvas generateProcessDiagram(BpmnModel bpmnModel, String imageType,List<String> highLightedFinishes, List<String> highLightedActivities, List<String> highLightedFlows, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader, double scaleFactor) {
        this.prepareBpmnModel(bpmnModel);
        DefaultProcessDiagramCanvas processDiagramCanvas = initProcessDiagramCanvas(bpmnModel, imageType, activityFontName, labelFontName, annotationFontName, customClassLoader);
        Iterator var12 = bpmnModel.getPools().iterator();

        while(var12.hasNext()) {
            Pool pool = (Pool)var12.next();
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            processDiagramCanvas.drawPoolOrLane(pool.getId(), pool.getName(), graphicInfo);
        }

        var12 = bpmnModel.getProcesses().iterator();

        Process process;
        Iterator var20;
        while(var12.hasNext()) {
            process = (Process)var12.next();
            var20 = process.getLanes().iterator();

            while(var20.hasNext()) {
                Lane lane = (Lane)var20.next();
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(lane.getId());
                processDiagramCanvas.drawPoolOrLane(lane.getId(), lane.getName(), graphicInfo);
            }
        }

        var12 = bpmnModel.getProcesses().iterator();
        while(var12.hasNext()) {
            process = (Process)var12.next();
            var20 = process.findFlowElementsOfType(FlowNode.class).iterator();

            while(var20.hasNext()) {
                FlowNode flowNode = (FlowNode)var20.next();
                this.drawActivity(processDiagramCanvas, bpmnModel, flowNode, highLightedFinishes,highLightedActivities, highLightedFlows, scaleFactor);
            }
        }

        var12 = bpmnModel.getProcesses().iterator();

        while(true) {
            List subProcesses;
            do {
                if(!var12.hasNext()) {
                    return processDiagramCanvas;
                }

                process = (Process)var12.next();
                var20 = process.getArtifacts().iterator();

                while(var20.hasNext()) {
                    Artifact artifact = (Artifact)var20.next();
                    this.drawArtifact(processDiagramCanvas, bpmnModel, artifact);
                }

                subProcesses = process.findFlowElementsOfType(SubProcess.class, true);
            } while(subProcesses == null);

            Iterator var24 = subProcesses.iterator();

            while(var24.hasNext()) {
                SubProcess subProcess = (SubProcess)var24.next();
                Iterator var17 = subProcess.getArtifacts().iterator();

                while(var17.hasNext()) {
                    Artifact subProcessArtifact = (Artifact)var17.next();
                    this.drawArtifact(processDiagramCanvas, bpmnModel, subProcessArtifact);
                }
            }
        }
    }

    protected static DefaultProcessDiagramCanvas initProcessDiagramCanvas(BpmnModel bpmnModel, String imageType, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader) {
        double minX = 1.7976931348623157E308D;
        double maxX = 0.0D;
        double minY = 1.7976931348623157E308D;
        double maxY = 0.0D;

        GraphicInfo graphicInfo;
        for(Iterator var14 = bpmnModel.getPools().iterator(); var14.hasNext(); maxY = graphicInfo.getY() + graphicInfo.getHeight()) {
            Pool pool = (Pool)var14.next();
            graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            minX = graphicInfo.getX();
            maxX = graphicInfo.getX() + graphicInfo.getWidth();
            minY = graphicInfo.getY();
        }

        List<FlowNode> flowNodes = gatherAllFlowNodes(bpmnModel);
        Iterator var24 = flowNodes.iterator();

        label155:
        while(var24.hasNext()) {
            FlowNode flowNode = (FlowNode)var24.next();
            GraphicInfo flowNodeGraphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            if(flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth() > maxX) {
                maxX = flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth();
            }

            if(flowNodeGraphicInfo.getX() < minX) {
                minX = flowNodeGraphicInfo.getX();
            }

            if(flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight() > maxY) {
                maxY = flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight();
            }

            if(flowNodeGraphicInfo.getY() < minY) {
                minY = flowNodeGraphicInfo.getY();
            }

            Iterator var18 = flowNode.getOutgoingFlows().iterator();

            while(true) {
                List graphicInfoList;
                do {
                    if(!var18.hasNext()) {
                        continue label155;
                    }

                    SequenceFlow sequenceFlow = (SequenceFlow)var18.next();
                    graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
                } while(graphicInfoList == null);

                Iterator var21 = graphicInfoList.iterator();

                while(var21.hasNext()) {
                    graphicInfo = (GraphicInfo)var21.next();
                    if(graphicInfo.getX() > maxX) {
                        maxX = graphicInfo.getX();
                    }

                    if(graphicInfo.getX() < minX) {
                        minX = graphicInfo.getX();
                    }

                    if(graphicInfo.getY() > maxY) {
                        maxY = graphicInfo.getY();
                    }

                    if(graphicInfo.getY() < minY) {
                        minY = graphicInfo.getY();
                    }
                }
            }
        }

        List<Artifact> artifacts = gatherAllArtifacts(bpmnModel);
        Iterator var27 = artifacts.iterator();

        while(var27.hasNext()) {
            Artifact artifact = (Artifact)var27.next();
            GraphicInfo artifactGraphicInfo = bpmnModel.getGraphicInfo(artifact.getId());
            if(artifactGraphicInfo != null) {
                if(artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth() > maxX) {
                    maxX = artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth();
                }

                if(artifactGraphicInfo.getX() < minX) {
                    minX = artifactGraphicInfo.getX();
                }

                if(artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight() > maxY) {
                    maxY = artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight();
                }

                if(artifactGraphicInfo.getY() < minY) {
                    minY = artifactGraphicInfo.getY();
                }
            }

            List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(artifact.getId());
            if(graphicInfoList != null) {
                Iterator var35 = graphicInfoList.iterator();

                while(var35.hasNext()) {
                    graphicInfo = (GraphicInfo)var35.next();
                    if(graphicInfo.getX() > maxX) {
                        maxX = graphicInfo.getX();
                    }

                    if(graphicInfo.getX() < minX) {
                        minX = graphicInfo.getX();
                    }

                    if(graphicInfo.getY() > maxY) {
                        maxY = graphicInfo.getY();
                    }

                    if(graphicInfo.getY() < minY) {
                        minY = graphicInfo.getY();
                    }
                }
            }
        }

        int nrOfLanes = 0;
        Iterator var30 = bpmnModel.getProcesses().iterator();

        while(var30.hasNext()) {
            Process process = (Process)var30.next();
            Iterator var34 = process.getLanes().iterator();

            while(var34.hasNext()) {
                Lane l = (Lane)var34.next();
                ++nrOfLanes;
                graphicInfo = bpmnModel.getGraphicInfo(l.getId());
                if(graphicInfo.getX() + graphicInfo.getWidth() > maxX) {
                    maxX = graphicInfo.getX() + graphicInfo.getWidth();
                }

                if(graphicInfo.getX() < minX) {
                    minX = graphicInfo.getX();
                }

                if(graphicInfo.getY() + graphicInfo.getHeight() > maxY) {
                    maxY = graphicInfo.getY() + graphicInfo.getHeight();
                }

                if(graphicInfo.getY() < minY) {
                    minY = graphicInfo.getY();
                }
            }
        }

        if(flowNodes.isEmpty() && bpmnModel.getPools().isEmpty() && nrOfLanes == 0) {
            minX = 0.0D;
            minY = 0.0D;
        }

        return new DefaultProcessDiagramCanvas((int)maxX + 10, (int)maxY + 10, (int)minX, (int)minY, imageType, activityFontName, labelFontName, annotationFontName, customClassLoader);
    }

    protected void drawActivity(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode,List<String> highLightedFinishes, List<String> highLightedActivities, List<String> highLightedFlows, double scaleFactor) {
        DefaultProcessDiagramGenerator.ActivityDrawInstruction drawInstruction = this.activityDrawInstructions.get(flowNode.getClass());
        boolean highLighted;
        if(drawInstruction != null) {
            drawInstruction.draw(processDiagramCanvas, bpmnModel, flowNode);
            boolean multiInstanceSequential = false;
            boolean multiInstanceParallel = false;
            highLighted = false;
            if(flowNode instanceof Activity) {
                Activity activity = (Activity)flowNode;
                MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = activity.getLoopCharacteristics();
                if(multiInstanceLoopCharacteristics != null) {
                    multiInstanceSequential = multiInstanceLoopCharacteristics.isSequential();
                    multiInstanceParallel = !multiInstanceSequential;
                }
            }

            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            if(!(flowNode instanceof SubProcess)) {
                if(flowNode instanceof CallActivity) {
                    highLighted = true;
                }
            } else {
                highLighted = graphicInfo.getExpanded() != null && !graphicInfo.getExpanded().booleanValue();
            }

            if(scaleFactor == 1.0D) {
                processDiagramCanvas.drawActivityMarkers((int)graphicInfo.getX(), (int)graphicInfo.getY(), (int)graphicInfo.getWidth(), (int)graphicInfo.getHeight(), multiInstanceSequential, multiInstanceParallel, highLighted);
            }

            if(highLightedActivities.contains(flowNode.getId())) {
                drawHighLight(processDiagramCanvas, bpmnModel.getGraphicInfo(flowNode.getId()),DefaultProcessDiagramCanvas.HIGHLIGHT_COLOR,flowNode);
            }

            if(highLightedFinishes.contains(flowNode.getId()) && !highLightedActivities.contains(flowNode.getId())) {
                drawHighLight(processDiagramCanvas, bpmnModel.getGraphicInfo(flowNode.getId()),DefaultProcessDiagramCanvas.FINISHHIGHLIGHT_COLOR,flowNode);
            }
        }

        Iterator var25 = flowNode.getOutgoingFlows().iterator();

        while(var25.hasNext()) {
            SequenceFlow sequenceFlow = (SequenceFlow)var25.next();
            highLighted = highLightedFlows.contains(sequenceFlow.getId());
            String defaultFlow = null;
            if(flowNode instanceof Activity) {
                defaultFlow = ((Activity)flowNode).getDefaultFlow();
            } else if(flowNode instanceof Gateway) {
                defaultFlow = ((Gateway)flowNode).getDefaultFlow();
            }

            boolean isDefault = false;
            if(defaultFlow != null && defaultFlow.equalsIgnoreCase(sequenceFlow.getId())) {
                isDefault = true;
            }

            boolean drawConditionalIndicator = sequenceFlow.getConditionExpression() != null && !(flowNode instanceof Gateway);
            String sourceRef = sequenceFlow.getSourceRef();
            String targetRef = sequenceFlow.getTargetRef();
            FlowElement sourceElement = bpmnModel.getFlowElement(sourceRef);
            FlowElement targetElement = bpmnModel.getFlowElement(targetRef);
            List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
            if(graphicInfoList != null && graphicInfoList.size() > 0) {
                graphicInfoList = connectionPerfectionizer(processDiagramCanvas, bpmnModel, sourceElement, targetElement, graphicInfoList);
                int[] xPoints = new int[graphicInfoList.size()];
                int[] yPoints = new int[graphicInfoList.size()];

                for(int i = 1; i < graphicInfoList.size(); ++i) {
                    GraphicInfo graphicInfo = (GraphicInfo)graphicInfoList.get(i);
                    GraphicInfo previousGraphicInfo = (GraphicInfo)graphicInfoList.get(i - 1);
                    if(i == 1) {
                        xPoints[0] = (int)previousGraphicInfo.getX();
                        yPoints[0] = (int)previousGraphicInfo.getY();
                    }

                    xPoints[i] = (int)graphicInfo.getX();
                    yPoints[i] = (int)graphicInfo.getY();
                }

                processDiagramCanvas.drawSequenceflow(xPoints, yPoints, drawConditionalIndicator, isDefault, highLighted, scaleFactor);
                GraphicInfo labelGraphicInfo = bpmnModel.getLabelGraphicInfo(sequenceFlow.getId());
                if(labelGraphicInfo != null) {
                    processDiagramCanvas.drawLabel(sequenceFlow.getName(), labelGraphicInfo, false);
                }
            }
        }

        if(flowNode instanceof FlowElementsContainer) {
            var25 = ((FlowElementsContainer)flowNode).getFlowElements().iterator();

            while(var25.hasNext()) {
                FlowElement nestedFlowElement = (FlowElement)var25.next();
                if(nestedFlowElement instanceof FlowNode) {
                    this.drawActivity(processDiagramCanvas, bpmnModel, (FlowNode)nestedFlowElement, highLightedFinishes,highLightedActivities, highLightedFlows, scaleFactor);
                }
            }
        }

    }
}
