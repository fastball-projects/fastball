import { Route, RouteProps } from "react-router-dom";
import { PageContainer } from '@ant-design/pro-layout';
import { Button } from 'antd';
import { MenuItemRoute } from '../types'
import DevConfig from '../dev-mode-config.json'


export const routeBuilder = (menu: MenuItemRoute) => {
    const routes = menu.routes ? menu.routes.map(routeBuilder) : []
    const routeProps: RouteProps = { path: menu.path }
    if (menu.component) {
        const MenuItemComponent = menu.component
        let editorUrl = `//${DevConfig.editorHost}:${DevConfig.editorPort}/fastball-editor/index.html?className=${menu.componentFullName}`
        if (DevConfig.proxyTarget) {
            editorUrl += `&proxyTarget=${DevConfig.proxyTarget}`
        }
        routeProps.element = (
            <PageContainer extra={<a target="_blank" href={editorUrl}><Button type="primary">调整界面</Button></a>}>
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