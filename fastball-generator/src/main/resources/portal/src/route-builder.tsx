import { Route, RouteProps } from "react-router-dom";
import { PageContainer } from '@ant-design/pro-layout';
import { MenuItemRoute } from '../types'


export const routeBuilder = (menu: MenuItemRoute) => {
    const routes = menu.routes ? menu.routes.map(routeBuilder) : []
    const routeProps: RouteProps = { path: menu.path }
    if (menu.component) {
        const MenuItemComponent = menu.component
        routeProps.element = (
            <PageContainer>
                <MenuItemComponent input={menu.params} />
            </PageContainer>
        )
    }
    return (
        <Route key={menu.path} {...routeProps}>
            {routes}
        </Route>
    )
}