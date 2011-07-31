package testSystem.lang.geom;

import ru.ipo.structurededitor.model.EnumFieldParams;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.03.11
 * Time: 14:39
 */
public enum Instrum {
    @EnumFieldParams(displayText = "Прямая по двум точкам")
    LINE_TWO_POINTS,
    @EnumFieldParams(displayText = "Параллельная прямая")
    LINE_PARALL,
    @EnumFieldParams(displayText = "Точка")
    POINT,
    @EnumFieldParams(displayText = "Перпендикулярная прямая")
    LINE_PERPEND,
    @EnumFieldParams(displayText = "Окружность по центру и радиусу")
    CIRCLE_CENTER_RAD,
    @EnumFieldParams(displayText = "Середина отрезка")
    MIDPOINT,
    @EnumFieldParams(displayText = "Отрезок по двум точкам")
    SEGMENT_TWO_POINTS,
    @EnumFieldParams(displayText = "Угол заданной величины")
    ANGLE_FIXED,
    @EnumFieldParams(displayText = "Окружность по центру и точке")
    CIRCLE_CENTER_POINT,
    @EnumFieldParams(displayText = "Угол по трем точкам")
    ANGLE_THREE_POINTS
}
