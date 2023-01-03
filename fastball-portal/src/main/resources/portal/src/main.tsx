import React, { useState } from 'react'
import ReactDOM from 'react-dom/client'
import { HashRouter, Routes, Route, Outlet, Link } from "react-router-dom";
import ProLayout, { PageContainer } from '@ant-design/pro-layout';
import { Button, Result } from 'antd';
import routes from './routes'

const HomePage = ({ routes }) => (
  <Routes>
    <Route path="/" element={<Layout routes={routes}/>}>
      {routes.map(buildRoutes)}
      <Route path="*" element={<Page404 />} />
    </Route>
  </Routes>
)

const buildRoutes = (route) => {
  const routes = route.routes ? route.routes.map(buildRoutes) : []
  return (
      <Route path={route.path} element={route.component}>
          {routes}
      </Route>
  )
}

const Page404 = () => (
  <Result
    status="404"
    style={{
      height: '100%',
    }}
    title="Hello World"
    subTitle="Sorry, you are not authorized to access this page."
    extra={<Button type="primary">Back Home</Button>}
  />
)

const Layout = ({ routes }) => {
  const [pathname, setPathname] = useState('/welcome');
  return (
    <div
      id="test-pro-layout"
      style={{
        height: '100vh',
      }}
    >
      <ProLayout
        fixSiderbar
        route={{
          path: '/',
          routes,
        }}
        location={{
          pathname,
        }}
        menu={{ defaultOpenAll: true, hideMenuWhenCollapsed: true }}
        menuItemRender={(item, dom) => (
          <a
            onClick={() => {
              setPathname(item.path || '/welcome');
            }}
          >
            <Link to={item.path}>{dom}</Link>
          </a>
        )}
      >
        <PageContainer>
          <Outlet />
        </PageContainer>
      </ProLayout>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <HashRouter>
      <HomePage routes={routes}/>
    </HashRouter>
  </React.StrictMode>,
)
